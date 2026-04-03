package com.example.dosagecalc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType

@Entity(tableName = "custom_drugs")
data class CustomDrugEntity(
    @PrimaryKey val id: String,
    val name: String,
    val indication: String,
    val formulaType: String,
    val unitDose: Double,
    val unitDoseMax: Double?,
    val unit: String,
    val minWeightKg: Double?,
    val maxWeightKg: Double?,
    val minAgeYears: Int?,
    val maxSingleDoseMcg: Double?,
    val alert: String,
    val source: String,
    val renalDoseMultiplier: Double?,
    val hepaticDoseMultiplier: Double?,
    val renalAlert: String?,
    val hepaticAlert: String?
) {
    fun toDomain(): Drug {
        return Drug(
            id = id,
            name = name,
            indication = indication,
            formulaType = FormulaType.valueOf(formulaType),
            unitDose = unitDose,
            unitDoseMax = unitDoseMax,
            unit = unit,
            minWeightKg = minWeightKg,
            maxWeightKg = maxWeightKg,
            minAgeYears = minAgeYears,
            maxSingleDoseMcg = maxSingleDoseMcg,
            alert = alert,
            source = source,
            renalDoseMultiplier = renalDoseMultiplier,
            hepaticDoseMultiplier = hepaticDoseMultiplier,
            renalAlert = renalAlert,
            hepaticAlert = hepaticAlert
        )
    }
}

fun Drug.toEntity(): CustomDrugEntity {
    return CustomDrugEntity(
        id = id,
        name = name,
        indication = indication,
        formulaType = formulaType.name,
        unitDose = unitDose,
        unitDoseMax = unitDoseMax,
        unit = unit,
        minWeightKg = minWeightKg,
        maxWeightKg = maxWeightKg,
        minAgeYears = minAgeYears,
        maxSingleDoseMcg = maxSingleDoseMcg,
        alert = alert,
        source = source,
        renalDoseMultiplier = renalDoseMultiplier,
        hepaticDoseMultiplier = hepaticDoseMultiplier,
        renalAlert = renalAlert,
        hepaticAlert = hepaticAlert
    )
}

