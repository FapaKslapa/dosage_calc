package com.example.dosagecalc.domain.model

data class CalculationHistory(
    val id: Int = 0,
    val timestamp: Long,
    val drugId: String,
    val drugName: String,
    val patientWeightKg: Double?,
    val patientHeightCm: Double?,
    val patientAgeYears: Int?,
    val totalDose: Double,
    val totalDoseMax: Double?,
    val unit: String,
    val formulaUsed: String
)

