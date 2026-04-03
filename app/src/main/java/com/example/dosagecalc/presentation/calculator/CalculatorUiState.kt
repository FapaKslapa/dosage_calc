package com.example.dosagecalc.presentation.calculator

import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.Drug

/**
 * Stato immutabile dell'intera schermata calcolatore (Unidirectional Data Flow).
 *
 * Un unico oggetto di stato copre tutte e 3 le schermate del flusso:
 * selezione → input → risultato. Il ViewModel aggiorna questo stato
 * tramite copy(), la UI osserva e si ridisegna solo dove necessario.
 *
 * L'immutabilità garantisce che la UI sia sempre consistente con lo stato:
 * non ci sono variabili "sparse" da sincronizzare manualmente.
 */
data class CalculatorUiState(

    // --- Stato caricamento farmaci ---
    /** True mentre il repository sta caricando la lista farmaci */
    val isLoadingDrugs: Boolean = true,

    /** Lista di tutti i farmaci disponibili, popolata all'avvio */
    val availableDrugs: List<Drug> = emptyList(),

    /** Errore di caricamento del catalogo farmaci (null = nessun errore) */
    val loadError: String? = null,

    // --- Selezione farmaco (Schermata 1) ---
    /** Farmaco correntemente selezionato dall'utente (null = nessuna selezione) */
    val selectedDrug: Drug? = null,

    // --- Input paziente (Schermata 2) ---
    /** Testo grezzo del campo peso: lasciamo String per validare al submit */
    val weightInput: String = "",
    val heightInput: String = "",
    val ageInput: String = "",

    /** Errori di validazione per i singoli campi (null = campo valido) */
    val weightError: String? = null,
    val heightError: String? = null,
    val ageError: String? = null,

    // --- Risultato calcolo (Schermata 3) ---
    /** Risultato del calcolo: null finché l'utente non preme "Calcola" */
    val dosageResult: DosageResult? = null,

    /** True durante l'elaborazione del calcolo (mostra loading indicator) */
    val isCalculating: Boolean = false
) {
    /**
     * Helper: true se tutti i campi obbligatori per il farmaco selezionato
     * sono compilati e non ci sono errori. Usato dalla UI per abilitare/disabilitare
     * il bottone "Calcola".
     */
    val canCalculate: Boolean
        get() = selectedDrug != null &&
                weightInput.isNotBlank() &&
                weightError == null &&
                heightError == null &&
                ageError == null
}
