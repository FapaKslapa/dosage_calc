package com.example.dosagecalc.data.model

import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class DrugDto(
    val id: String,
    val name: String,
    val indication: String,
    val formulaType: String,
    val unitDose: Double,
    val unitDoseMax: Double? = null,
    val unit: String,
    val minWeightKg: Double? = null,
    val maxWeightKg: Double? = null,
    val minAgeYears: Int? = null,
    val maxSingleDoseMcg: Double? = null,
    val alert: String,
    val source: String,
    val category: String? = null,
    val renalDoseMultiplier: Double? = null,
    val hepaticDoseMultiplier: Double? = null,
    val renalAlert: String? = null,
    val hepaticAlert: String? = null,
    val daysPerCycle: Int? = null,
    val numberOfCycles: Int? = null,
    val contraindications: String? = null,
    val sideEffects: String? = null
) {
    fun toDomain(): Drug = Drug(
        id                    = id,
        name                  = name,
        indication            = indication,
        formulaType           = when (formulaType.lowercase()) {
            "per_kg"          -> FormulaType.PER_KG
            "per_m2"          -> FormulaType.PER_M2
            "fixed"           -> FormulaType.FIXED
            "by_range"        -> FormulaType.BY_RANGE
            else              -> FormulaType.FIXED
        },
        unitDose              = unitDose,
        unitDoseMax           = unitDoseMax,
        unit                  = unit,
        minWeightKg           = minWeightKg,
        maxWeightKg           = maxWeightKg,
        minAgeYears           = minAgeYears,
        maxSingleDoseMcg      = maxSingleDoseMcg,
        alert                 = alert,
        source                = source,
        category              = when (category?.lowercase()) {
            "dermatology"     -> com.example.dosagecalc.domain.model.DrugCategory.DERMATOLOGY
            "oncology"        -> com.example.dosagecalc.domain.model.DrugCategory.ONCOLOGY
            "infectious"      -> com.example.dosagecalc.domain.model.DrugCategory.INFECTIOUS
            "pediatrics"      -> com.example.dosagecalc.domain.model.DrugCategory.PEDIATRICS
            else              -> com.example.dosagecalc.domain.model.DrugCategory.OTHER
        },
        renalDoseMultiplier   = renalDoseMultiplier,
        hepaticDoseMultiplier = hepaticDoseMultiplier,
        renalAlert            = renalAlert,
        hepaticAlert          = hepaticAlert,
        daysPerCycle          = daysPerCycle,
        numberOfCycles        = numberOfCycles,
        contraindications     = contraindications,
        sideEffects           = sideEffects
    )
}
