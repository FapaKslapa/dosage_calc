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

            FormulaType.FIXED -> calculateFixed(drug, patientData)

            FormulaType.BY_RANGE -> calculateByRange(drug, patientData)
        }
    }

    private fun applyImpairments(rawDose: Double, drug: Drug, patientData: PatientData): Pair<Double, String> {
        var dose = rawDose
        var alertMessage = ""

        if (patientData.hasRenalImpairment && drug.renalDoseMultiplier != null) {
            dose *= drug.renalDoseMultiplier
            alertMessage += "⚠ Dose ridotta del ${((1 - drug.renalDoseMultiplier) * 100).toInt()}% per insufficienza renale. "
            if (drug.renalAlert != null) alertMessage += "${drug.renalAlert} "
        }

        if (patientData.hasHepaticImpairment && drug.hepaticDoseMultiplier != null) {
            dose *= drug.hepaticDoseMultiplier
            alertMessage += "\n⚠ Dose ridotta del ${((1 - drug.hepaticDoseMultiplier) * 100).toInt()}% per insufficienza epatica. "
            if (drug.hepaticAlert != null) alertMessage += "${drug.hepaticAlert} "
        }

        return Pair(dose, alertMessage)
    }

    private fun calculateByRange(drug: Drug, patientData: PatientData): DosageResult {
        val weight = patientData.weightKg!! 
        val rawMinDose = drug.unitDose * weight
        val rawMaxDose = (drug.unitDoseMax ?: drug.unitDose) * weight

        val (impairedMinDose, impAlertMin) = applyImpairments(rawMinDose, drug, patientData)
        val (impairedMaxDose, impAlertMax) = applyImpairments(rawMaxDose, drug, patientData)

        val (finalMin, cappedMin) = applyCeiling(impairedMinDose, drug.maxSingleDoseMcg)
        val (finalMax, cappedMax) = applyCeiling(impairedMaxDose, drug.maxSingleDoseMcg)

        val capped = cappedMin || cappedMax
        var formula = "Intervallo base: ${drug.unitDose} - ${drug.unitDoseMax ?: drug.unitDose} ${drug.unit}/kg × $weight kg = $rawMinDose - $rawMaxDose ${drug.unit}."
        if (impAlertMin.isNotBlank()) formula += "\nPenalità patologia applicata."

        if (capped) formula += "\n(Ridotta al massimo consentito: ${drug.maxSingleDoseMcg})"

        val fullAlert = sequenceOf(drug.alert, impAlertMin).filter { it.isNotBlank() }.joinToString("\n\n").trim()

        return DosageResult.Success(
            totalDose       = finalMin,
            totalDoseMax    = finalMax,
            unit            = drug.unit,
            formula         = formula,
            alert           = fullAlert,
            source          = drug.source,
            cappedToMaxDose = capped
        )
    }

    private fun calculatePerKg(drug: Drug, patientData: PatientData): DosageResult {
        val weight = patientData.weightKg!!  
        val rawDose = drug.unitDose * weight

        val (impairedDose, impAlert) = applyImpairments(rawDose, drug, patientData)

        val (finalDose, capped) = applyCeiling(impairedDose, drug.maxSingleDoseMcg)

        var formula = "${drug.unitDose} ${drug.unit}/kg × $weight kg = $rawDose ${drug.unit}"
        if (impAlert.isNotBlank()) formula += "\n(Riduzione patologica applicata)"
        if (capped) formula += " (limitato al massimo consentito: ${drug.maxSingleDoseMcg})"

        val fullAlert = sequenceOf(drug.alert, impAlert).filter { it.isNotBlank() }.joinToString("\n\n").trim()

        return DosageResult.Success(
            totalDose       = finalDose,
            unit            = drug.unit,
            formula         = formula,
            alert           = fullAlert,
            source          = drug.source,
            cappedToMaxDose = capped
        )
    }

    private fun calculatePerM2(drug: Drug, patientData: PatientData): DosageResult {
        val weight = patientData.weightKg!!   
        val height = patientData.heightCm!!   

        val bsa = calculateBsaUseCase(weight, height)
        val rawDose = drug.unitDose * bsa

        val (impairedDose, impAlert) = applyImpairments(rawDose, drug, patientData)

        val (finalDose, capped) = applyCeiling(impairedDose, drug.maxSingleDoseMcg)

        var formula = "${drug.unitDose} ${drug.unit}/m² × $bsa m² (BSA) = $rawDose ${drug.unit}"
        if (impAlert.isNotBlank()) formula += "\n(Riduzione patologica applicata)"
        if (capped) formula += " (limitato al massimo consentito)"

        val fullAlert = sequenceOf(drug.alert, impAlert).filter { it.isNotBlank() }.joinToString("\n\n").trim()

        return DosageResult.Success(
            totalDose       = finalDose,
            unit            = drug.unit,
            formula         = formula,
            alert           = fullAlert,
            source          = drug.source,
            cappedToMaxDose = capped
        )
    }

    private fun calculateFixed(drug: Drug, patientData: PatientData): DosageResult {
        val rawDose = drug.unitDose
        val (impairedDose, impAlert) = applyImpairments(rawDose, drug, patientData)
        val formula = "Dose fissa: ${drug.unitDose} ${drug.unit}"

        val fullAlert = sequenceOf(drug.alert, impAlert).filter { it.isNotBlank() }.joinToString("\n\n").trim()

        return DosageResult.Success(
            totalDose       = impairedDose,
            unit            = drug.unit,
            formula         = formula,
            alert           = fullAlert,
            source          = drug.source,
            cappedToMaxDose = false
        )
    }

    private fun applyCeiling(dose: Double, maxDose: Double?): Pair<Double, Boolean> {
        if (maxDose == null || dose <= maxDose) return Pair(dose, false)
        return Pair(maxDose, true)
    }
}
