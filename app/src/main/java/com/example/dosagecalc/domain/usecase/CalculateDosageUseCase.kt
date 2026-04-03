package com.example.dosagecalc.domain.usecase

import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType
import com.example.dosagecalc.domain.model.PatientData
import javax.inject.Inject

class CalculateDosageUseCase @Inject constructor(
    private val validateInputUseCase: ValidateInputUseCase,
    private val calculateBsaUseCase: CalculateBsaUseCase
) {

    operator fun invoke(drug: Drug, patientData: PatientData): DosageResult {

        val validation = validateInputUseCase(drug, patientData)
        if (validation is ValidationResult.Invalid) {
            
            return DosageResult.ValidationError(
                reason = validation.errors.joinToString(separator = "\n• ", prefix = "• ")
            )
        }

        return when (drug.formulaType) {

            FormulaType.PER_KG -> calculatePerKg(drug, patientData)

            FormulaType.PER_M2 -> calculatePerM2(drug, patientData)

            FormulaType.FIXED -> calculateFixed(drug)

            FormulaType.BY_RANGE -> calculateByRange(drug, patientData)
        }
    }

    private fun calculateByRange(drug: Drug, patientData: PatientData): DosageResult {
        val weight = patientData.weightKg!! 
        val rawMinDose = drug.unitDose * weight
        val rawMaxDose = (drug.unitDoseMax ?: drug.unitDose) * weight

        val (finalMin, cappedMin) = applyCeiling(rawMinDose, drug.maxSingleDoseMcg)
        val (finalMax, cappedMax) = applyCeiling(rawMaxDose, drug.maxSingleDoseMcg)

        val capped = cappedMin || cappedMax
        val formula = "Intervallo: ${drug.unitDose} - ${drug.unitDoseMax ?: drug.unitDose} ${drug.unit}/kg × $weight kg = $finalMin - $finalMax ${drug.unit}" +
                if (capped) " (ridotta al massimo consentito)" else ""

        return DosageResult.Success(
            totalDose       = finalMin,
            totalDoseMax    = finalMax,
            unit            = drug.unit,
            formula         = formula,
            alert           = drug.alert,
            source          = drug.source,
            cappedToMaxDose = capped
        )
    }

    private fun calculatePerKg(drug: Drug, patientData: PatientData): DosageResult {
        val weight = patientData.weightKg!!  
        val rawDose = drug.unitDose * weight

        val (finalDose, capped) = applyCeiling(rawDose, drug.maxSingleDoseMcg)

        val formula = "${drug.unitDose} ${drug.unit}/kg × $weight kg = $finalDose ${drug.unit}" +
                if (capped) " (ridotta al massimo consentito)" else ""

        return DosageResult.Success(
            totalDose       = finalDose,
            unit            = drug.unit,
            formula         = formula,
            alert           = drug.alert,
            source          = drug.source,
            cappedToMaxDose = capped
        )
    }

    private fun calculatePerM2(drug: Drug, patientData: PatientData): DosageResult {
        val weight = patientData.weightKg!!   
        val height = patientData.heightCm!!   

        val bsa = calculateBsaUseCase(weight, height)
        val rawDose = drug.unitDose * bsa

        val (finalDose, capped) = applyCeiling(rawDose, drug.maxSingleDoseMcg)

        val formula = "${drug.unitDose} ${drug.unit}/m² × $bsa m² (BSA) = $finalDose ${drug.unit}" +
                if (capped) " (ridotta al massimo consentito)" else ""

        return DosageResult.Success(
            totalDose       = finalDose,
            unit            = drug.unit,
            formula         = formula,
            alert           = drug.alert,
            source          = drug.source,
            cappedToMaxDose = capped
        )
    }

    private fun calculateFixed(drug: Drug): DosageResult {
        val formula = "Dose fissa: ${drug.unitDose} ${drug.unit}"

        return DosageResult.Success(
            totalDose       = drug.unitDose,
            unit            = drug.unit,
            formula         = formula,
            alert           = drug.alert,
            source          = drug.source,
            cappedToMaxDose = false
        )
    }

    private fun applyCeiling(dose: Double, maxDose: Double?): Pair<Double, Boolean> {
        if (maxDose == null || dose <= maxDose) return Pair(dose, false)
        return Pair(maxDose, true)
    }
}
