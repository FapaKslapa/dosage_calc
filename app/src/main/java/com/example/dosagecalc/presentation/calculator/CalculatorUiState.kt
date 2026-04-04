package com.example.dosagecalc.presentation.calculator

import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.DrugCategory
import com.example.dosagecalc.domain.model.HepaticStage
import com.example.dosagecalc.domain.model.Patient
import com.example.dosagecalc.domain.model.RenalStage

data class CalculatorUiState(
    val isLoadingDrugs: Boolean = true,
    val availableDrugs: List<Drug> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: DrugCategory? = null,
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
    val renalStage: RenalStage = RenalStage.NONE,
    val hepaticStage: HepaticStage = HepaticStage.NONE,
    val bsaFormula: com.example.dosagecalc.domain.repository.BsaFormulaType = com.example.dosagecalc.domain.repository.BsaFormulaType.MOSTELLER
) {
    val filteredDrugs: List<Drug>
        get() = availableDrugs
            .filter { drug ->
                val matchesSearch = drug.name.contains(searchQuery, ignoreCase = true) ||
                                    drug.indication.contains(searchQuery, ignoreCase = true)
                val matchesCategory = selectedCategory == null || drug.category == selectedCategory
                matchesSearch && matchesCategory
            }

    val canCalculate: Boolean
        get() = selectedDrug != null &&
                weightInput.isNotBlank() &&
                weightError == null &&
                heightError == null &&
                ageError == null
}
