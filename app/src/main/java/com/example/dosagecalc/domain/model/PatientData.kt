package com.example.dosagecalc.domain.model

data class PatientData(
    
    val weightKg: Double?,

    val heightCm: Double?,

    val ageYears: Int?,

    val hasRenalImpairment: Boolean = false,

    val hasHepaticImpairment: Boolean = false
)
