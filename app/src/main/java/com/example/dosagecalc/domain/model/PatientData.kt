package com.example.dosagecalc.domain.model

import com.example.dosagecalc.domain.repository.BsaFormulaType

data class PatientData(
    val weightKg: Double?,
    val heightCm: Double?,
    val ageYears: Int?,
    val renalStage: RenalStage = RenalStage.NONE,
    val hepaticStage: HepaticStage = HepaticStage.NONE,
    val bsaFormula: BsaFormulaType = BsaFormulaType.MOSTELLER
) {
    val hasRenalImpairment: Boolean get() = renalStage != RenalStage.NONE
    val hasHepaticImpairment: Boolean get() = hepaticStage != HepaticStage.NONE
}
