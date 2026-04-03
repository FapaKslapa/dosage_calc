package com.example.dosagecalc.presentation.calculator

import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.Patient

data class CalculatorUiState(
    val isLoadingDrugs: Boolean = true,
    val availableDrugs: List<Drug> = emptyList(),
    val searchQuery: String = "",
    val loadError: String? = null,
    val savedPatients: List<Patient> = emptyList(),
    val selectedDrug: Drug? = null,
    val selectedPatient: Patient? = null,
    val weightInput: String = "",
    val heightInput: String = "",
    val ageInput: String = "",
    val weightError: String? = null,
    val heightError: String? = null,
    val ageError: String? = null,
    val dosageResult: DosageResult? = null,
    val isCalculating: Boolean = false,
    val hasRenalImpairment: Boolean = false,
    val hasHepaticImpairment: Boolean = false
) {
    val canCalculate: Boolean
        get() = selectedDrug != null &&
                weightInput.isNotBlank() &&
                weightError == null &&
                heightError == null &&
                ageError == null
}
