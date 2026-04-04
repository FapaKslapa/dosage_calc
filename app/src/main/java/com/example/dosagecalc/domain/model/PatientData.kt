package com.example.dosagecalc.domain.model

data class PatientData(
    val weightKg: Double?,
    val heightCm: Double?,
    val ageYears: Int?,
    val renalStage: RenalStage = RenalStage.NONE,
    val hepaticStage: HepaticStage = HepaticStage.NONE
) {
    val hasRenalImpairment: Boolean get() = renalStage != RenalStage.NONE
    val hasHepaticImpairment: Boolean get() = hepaticStage != HepaticStage.NONE
}
