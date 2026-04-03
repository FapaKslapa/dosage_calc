package com.example.dosagecalc.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history_records",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"])
    ]
)
data class HistoryEntity(
    @PrimaryKey
    val id: String,
    val patientId: String?,
    val drugId: String,
    val drugName: String,
    val date: Long, 
    val weightKg: Float,
    val heightCm: Float?,
    val ageYears: Int,
    val calculatedDose: Double,
    val doseUnit: String,
    val notes: String?
)

