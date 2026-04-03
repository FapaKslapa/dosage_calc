package com.example.dosagecalc.presentation.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.PatientData
import com.example.dosagecalc.domain.repository.DrugRepository
import com.example.dosagecalc.domain.usecase.CalculateDosageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel per il flusso calcolatore (Selezione → Input → Risultato).
 *
 * Responsabilità:
 * - Caricare la lista farmaci dal repository all'avvio
 * - Gestire lo stato dell'UI in modo reattivo tramite [StateFlow]
 * - Orchestrare il calcolo della dose tramite [CalculateDosageUseCase]
 * - Validare i campi di input in tempo reale (mentre l'utente digita)
 *
 * La UI NON chiama mai direttamente i Use Case: passa tutto attraverso
 * questo ViewModel. Questo garantisce che la logica non stia nella UI.
 *
 * @HiltViewModel permette a Hilt di iniettare le dipendenze e a
 * Navigation Compose di gestire il ciclo di vita correttamente.
 */
@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val drugRepository: DrugRepository,
    private val calculateDosageUseCase: CalculateDosageUseCase
) : ViewModel() {

    // Stato interno mutabile: privato, modificato solo dal ViewModel
    private val _uiState = MutableStateFlow(CalculatorUiState())

    // Stato esposto alla UI: read-only, la UI non può modificarlo direttamente
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        // Carica i farmaci subito alla creazione del ViewModel
        loadDrugs()
    }

    // -------------------------------------------------------------------------
    // Caricamento dati
    // -------------------------------------------------------------------------

    private fun loadDrugs() {
        viewModelScope.launch {
            drugRepository.getDrugs()
                .catch { error ->
                    // Se il JSON è corrotto o mancante, mostriamo un errore
                    // invece di lasciare l'app in uno stato di loading infinito
                    _uiState.update { it.copy(
                        isLoadingDrugs = false,
                        loadError = "Impossibile caricare il catalogo farmaci: ${error.message}"
                    )}
                }
                .collect { drugs ->
                    _uiState.update { it.copy(
                        isLoadingDrugs = false,
                        availableDrugs = drugs,
                        loadError = null
                    )}
                }
        }
    }

    // -------------------------------------------------------------------------
    // Azioni utente - Schermata 1: Selezione farmaco
    // -------------------------------------------------------------------------

    /**
     * Chiamato quando l'utente seleziona un farmaco dal dropdown.
     * Resetta il risultato precedente per evitare di mostrare un calcolo
     * relativo a un farmaco diverso da quello appena selezionato.
     */
    fun onDrugSelected(drug: Drug) {
        _uiState.update { it.copy(
            selectedDrug  = drug,
            dosageResult  = null,   // reset risultato precedente
            weightError   = null,
            heightError   = null,
            ageError      = null
        )}
    }

    // -------------------------------------------------------------------------
    // Azioni utente - Schermata 2: Input paziente
    // -------------------------------------------------------------------------

    /** Aggiorna il testo del peso e valida il formato in tempo reale */
    fun onWeightChanged(value: String) {
        val error = validateNumericField(value, min = 1.0, max = 500.0, fieldName = "peso")
        _uiState.update { it.copy(weightInput = value, weightError = error, dosageResult = null) }
    }

    /** Aggiorna il testo dell'altezza e valida in tempo reale */
    fun onHeightChanged(value: String) {
        val error = validateNumericField(value, min = 30.0, max = 280.0, fieldName = "altezza")
        _uiState.update { it.copy(heightInput = value, heightError = error, dosageResult = null) }
    }

    /** Aggiorna il testo dell'età e valida in tempo reale */
    fun onAgeChanged(value: String) {
        // L'età è un intero: validazione separata
        val error = if (value.isBlank()) null else {
            val age = value.toIntOrNull()
            when {
                age == null    -> "Inserire un numero intero"
                age < 0        -> "Età non valida"
                age > 120      -> "Età non valida (max 120)"
                else           -> null
            }
        }
        _uiState.update { it.copy(ageInput = value, ageError = error, dosageResult = null) }
    }

    // -------------------------------------------------------------------------
    // Azione principale: Calcolo dose
    // -------------------------------------------------------------------------

    /**
     * Esegue il calcolo della dose con i dati attuali dello stato.
     *
     * Il calcolo avviene su [Dispatchers.Default] per non bloccare il main thread,
     * poi il risultato viene emesso sullo stato (sempre sul main thread tramite update).
     * Per calcoli semplici come questo è un over-engineering, ma è la best practice
     * corretta per operazioni potenzialmente intensive.
     */
    fun calculateDosage() {
        val state = _uiState.value
        val drug = state.selectedDrug ?: return  // guard: non dovrebbe succedere

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true, dosageResult = null) }

            val result: DosageResult = withContext(Dispatchers.Default) {
                val patientData = PatientData(
                    weightKg  = state.weightInput.toDoubleOrNull(),
                    heightCm  = state.heightInput.toDoubleOrNull(),
                    ageYears  = state.ageInput.toIntOrNull()
                )
                calculateDosageUseCase(drug, patientData)
            }

            _uiState.update { it.copy(
                isCalculating = false,
                dosageResult  = result
            )}
        }
    }

    /** Resetta completamente il flusso per iniziare un nuovo calcolo */
    fun resetCalculation() {
        _uiState.update { it.copy(
            selectedDrug  = null,
            weightInput   = "",
            heightInput   = "",
            ageInput      = "",
            weightError   = null,
            heightError   = null,
            ageError      = null,
            dosageResult  = null
        )}
    }

    // -------------------------------------------------------------------------
    // Utility privata
    // -------------------------------------------------------------------------

    /**
     * Valida che un campo di testo contenga un numero Double nel range atteso.
     * @return messaggio di errore, o null se il campo è valido (o vuoto)
     */
    private fun validateNumericField(value: String, min: Double, max: Double, fieldName: String): String? {
        if (value.isBlank()) return null  // campo vuoto = non ancora compilato, non è un errore

        val number = value.toDoubleOrNull()
        return when {
            number == null -> "Inserire un numero valido"
            number < min   -> "$fieldName troppo basso (min $min)"
            number > max   -> "$fieldName troppo alto (max $max)"
            else           -> null
        }
    }
}
