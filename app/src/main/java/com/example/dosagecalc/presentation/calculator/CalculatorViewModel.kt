package com.example.dosagecalc.presentation.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.HistoryRecord
import com.example.dosagecalc.domain.model.Patient
import com.example.dosagecalc.domain.model.PatientData
import com.example.dosagecalc.domain.repository.DrugRepository
import com.example.dosagecalc.domain.usecase.CalculateDosageUseCase
import com.example.dosagecalc.domain.usecase.ManageHistoryUseCase
import com.example.dosagecalc.domain.usecase.ManagePatientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val drugRepository: DrugRepository,
    private val calculateDosageUseCase: CalculateDosageUseCase,
    private val managePatientsUseCase: ManagePatientsUseCase,
    private val manageHistoryUseCase: ManageHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())

    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        loadDrugs()
        loadPatients()
    }

    private fun loadPatients() {
        viewModelScope.launch {
            managePatientsUseCase.getPatients().collect { list ->
                _uiState.update { it.copy(savedPatients = list) }
            }
        }
    }

    private fun loadDrugs() {
        viewModelScope.launch {
            drugRepository.getDrugs()
                .catch { error ->

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

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onDrugSelected(drug: Drug) {
        _uiState.update { it.copy(
            selectedDrug  = drug,
            selectedPatient = null, 
            dosageResult  = null,   
            weightError   = null,
            heightError   = null,
            ageError      = null
        )}
    }

    fun onPatientSelected(patient: Patient?) {
        if (patient == null) {
            _uiState.update { it.copy(selectedPatient = null) }
        } else {
            
            _uiState.update { it.copy(
                selectedPatient = patient,
                weightInput = patient.weightKg.toString(),
                heightInput = patient.heightCm?.toString() ?: "",
                ageInput = patient.ageYears.toString(),
                hasRenalImpairment = patient.hasRenalImpairment,
                hasHepaticImpairment = patient.hasHepaticImpairment,
                weightError = null,
                heightError = null,
                ageError = null,
                dosageResult = null
            ) }
        }
    }

    fun onWeightChanged(value: String) {
        val error = validateNumericField(value, min = 1.0, max = 500.0, fieldName = "peso")
        _uiState.update { it.copy(weightInput = value, weightError = error, dosageResult = null) }
    }

    fun onHeightChanged(value: String) {
        val error = validateNumericField(value, min = 30.0, max = 280.0, fieldName = "altezza")
        _uiState.update { it.copy(heightInput = value, heightError = error, dosageResult = null) }
    }

    fun onAgeChanged(value: String) {
        
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

    fun deleteCustomDrug(id: String) {
        viewModelScope.launch {
            drugRepository.deleteCustomDrug(id)
        }
    }

    fun onRenalImpairmentChanged(checked: Boolean) {
        _uiState.update { it.copy(hasRenalImpairment = checked, dosageResult = null) }
    }

    fun onHepaticImpairmentChanged(checked: Boolean) {
        _uiState.update { it.copy(hasHepaticImpairment = checked, dosageResult = null) }
    }

    fun calculateDosage() {
        val state = _uiState.value
        val drug = state.selectedDrug ?: return  

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true, dosageResult = null) }

            val result: DosageResult = withContext(Dispatchers.Default) {
                val patientData = PatientData(
                    weightKg  = state.weightInput.toDoubleOrNull(),
                    heightCm  = state.heightInput.toDoubleOrNull(),
                    ageYears  = state.ageInput.toIntOrNull(),
                    hasRenalImpairment = state.hasRenalImpairment,
                    hasHepaticImpairment = state.hasHepaticImpairment
                )
                calculateDosageUseCase(drug, patientData)
            }

            if (result is DosageResult.Success) {
                val record = HistoryRecord(
                    id = "",
                    patientId = state.selectedPatient?.id,
                    drugId = drug.id,
                    drugName = drug.name,
                    date = LocalDateTime.now(),
                    weightKg = state.weightInput.toFloatOrNull() ?: 0f,
                    heightCm = state.heightInput.toFloatOrNull(),
                    ageYears = state.ageInput.toIntOrNull() ?: 0,
                    calculatedDose = result.totalDose,
                    calculatedDoseMax = result.totalDoseMax,
                    doseUnit = result.unit,
                    formulaUsed = result.formula,
                    notes = null
                )
                manageHistoryUseCase.saveHistoryRecord(record)
            }

            _uiState.update { it.copy(
                isCalculating = false,
                dosageResult  = result
            )}
        }
    }

    fun resetCalculation() {
        _uiState.update { it.copy(
            selectedDrug  = null,
            selectedPatient = null,
             weightInput   = "",
            heightInput   = "",
            ageInput      = "",
            weightError   = null,
            heightError   = null,
            ageError      = null,
            dosageResult  = null,
            hasRenalImpairment = false,
            hasHepaticImpairment = false
        )}
    }

    private fun validateNumericField(value: String, min: Double, max: Double, fieldName: String): String? {
        if (value.isBlank()) return null  

        val number = value.toDoubleOrNull()
        return when {
            number == null -> "Inserire un numero valido"
            number < min   -> "$fieldName troppo basso (min $min)"
            number > max   -> "$fieldName troppo alto (max $max)"
            else           -> null
        }
    }
}
