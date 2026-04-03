package com.example.dosagecalc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val surname: String,
    val birthDate: Long, 
    val weightKg: Float,
    val heightCm: Float?,
    val ageYears: Int,
    val notes: String?
)
