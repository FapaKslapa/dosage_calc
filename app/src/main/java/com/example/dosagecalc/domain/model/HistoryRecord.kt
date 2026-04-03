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
    val doseUnit: String,
    val notes: String? = null
)

