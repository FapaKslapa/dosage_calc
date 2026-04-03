package com.example.dosagecalc.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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

