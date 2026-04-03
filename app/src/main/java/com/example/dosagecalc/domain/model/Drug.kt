package com.example.dosagecalc.domain.model

data class Drug(
    
    val id: String,

    val name: String,

    val indication: String,

    val formulaType: FormulaType,

    val unitDose: Double,

    val unitDoseMax: Double? = null,

    val unit: String,

    val minWeightKg: Double?,

    val maxWeightKg: Double?,

    val minAgeYears: Int?,

    val maxSingleDoseMcg: Double?,

    val alert: String,

    val source: String
)

enum class FormulaType {
    
    PER_KG,

    PER_M2,

    FIXED,

    BY_RANGE
}
