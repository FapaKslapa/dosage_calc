package com.example.dosagecalc.data.model

import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class DrugDto(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("indication")
    val indication: String,

    @SerialName("formulaType")
    val formulaType: String,

    @SerialName("unitDose")
    val unitDose: Double,

    @SerialName("unitDoseMax")
    val unitDoseMax: Double? = null,

    @SerialName("unit")
    val unit: String,

    @SerialName("minWeightKg")
    val minWeightKg: Double? = null,

    @SerialName("maxWeightKg")
    val maxWeightKg: Double? = null,

    @SerialName("minAgeYears")
    val minAgeYears: Int? = null,

    @SerialName("maxSingleDoseMcg")
    val maxSingleDoseMcg: Double? = null,

    @SerialName("alert")
    val alert: String,

    @SerialName("source")
    val source: String,

    @SerialName("renalDoseMultiplier")
    val renalDoseMultiplier: Double? = null,

    @SerialName("hepaticDoseMultiplier")
    val hepaticDoseMultiplier: Double? = null,

    @SerialName("renalAlert")
    val renalAlert: String? = null,

    @SerialName("hepaticAlert")
    val hepaticAlert: String? = null

) {

    fun toDomain(): Drug = Drug(
        id              = id,
        name            = name,
        indication      = indication,
        formulaType     = when (formulaType.lowercase()) {
            "per_kg"    -> FormulaType.PER_KG
            "per_m2"    -> FormulaType.PER_M2
            "fixed"     -> FormulaType.FIXED
            "by_range"  -> FormulaType.BY_RANGE
            else        -> {

                FormulaType.FIXED
            }
        },
        unitDose        = unitDose,
        unitDoseMax     = unitDoseMax,
        unit            = unit,
        minWeightKg     = minWeightKg,
        maxWeightKg     = maxWeightKg,
        minAgeYears     = minAgeYears,
        maxSingleDoseMcg = maxSingleDoseMcg,
        alert           = alert,
        source          = source,
        renalDoseMultiplier = renalDoseMultiplier,
        hepaticDoseMultiplier = hepaticDoseMultiplier,
        renalAlert      = renalAlert,
        hepaticAlert    = hepaticAlert
    )
}
