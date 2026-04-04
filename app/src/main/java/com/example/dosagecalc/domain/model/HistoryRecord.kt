package com.example.dosagecalc.domain.model

import java.time.LocalDateTime

data class HistoryRecord(
    val id: String,
    val patientId: String?, 
    val drugId: String,
    val drugName: String,
    val date: LocalDateTime,
    val weightKg: Float,
    val heightCm: Float?,
    val ageYears: Int,
    val calculatedDose: Double,
    val calculatedDoseMax: Double? = null,
    val calculatedCycleDose: Double? = null,
    val calculatedTherapyDose: Double? = null,
    val doseUnit: String,
    val formulaUsed: String? = null,
    val notes: String? = null
)

