package com.example.dosagecalc.domain.model

import java.time.LocalDateTime

data class Patient(
    val id: String,
    val name: String,
    val surname: String,
    val birthDate: LocalDateTime,
    val weightKg: Float,
    val heightCm: Float?,
    val ageYears: Int,
    val notes: String? = null
)
