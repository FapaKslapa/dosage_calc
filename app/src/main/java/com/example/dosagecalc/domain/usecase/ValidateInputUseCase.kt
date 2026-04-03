package com.example.dosagecalc.domain.usecase

import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType
import com.example.dosagecalc.domain.model.PatientData
import javax.inject.Inject

class ValidateInputUseCase @Inject constructor() {

    operator fun invoke(drug: Drug, patientData: PatientData): ValidationResult {
        val errors = mutableListOf<String>()

        when (drug.formulaType) {
            FormulaType.PER_KG, FormulaType.BY_RANGE -> {
                if (patientData.weightKg == null) {
                    errors.add("Il peso è obbligatorio per questo farmaco.")
                }
            }
            FormulaType.PER_M2 -> {
                if (patientData.weightKg == null) {
                    errors.add("Il peso è obbligatorio per il calcolo del BSA.")
                }
                if (patientData.heightCm == null) {
                    errors.add("L'altezza è obbligatoria per il calcolo del BSA.")
                }
            }
            FormulaType.FIXED -> Unit
        }

        patientData.weightKg?.let { w ->
            if (w < 1.0 || w > 500.0) {
                errors.add("Peso non fisiologico: $w kg. Range accettato: 1–500 kg.")
            }
        }

        patientData.heightCm?.let { h ->
            if (h < 30.0 || h > 280.0) {
                errors.add("Altezza non fisiologica: $h cm. Range accettato: 30–280 cm.")
            }
        }

        patientData.ageYears?.let { age ->
            if (age < 0 || age > 120) {
                errors.add("Età non valida: $age anni.")
            }
        }

        drug.minWeightKg?.let { minW ->
            patientData.weightKg?.let { w ->
                if (w < minW) {
                    errors.add(
                        "Peso insufficiente per ${drug.name}: " +
                        "minimo richiesto ${minW} kg, paziente ${w} kg."
                    )
                }
            }
        }

        drug.maxWeightKg?.let { maxW ->
            patientData.weightKg?.let { w ->
                if (w > maxW) {
                    errors.add(
                        "Peso superiore al limite per ${drug.name}: " +
                        "massimo ${maxW} kg, paziente ${w} kg."
                    )
                }
            }
        }

        drug.minAgeYears?.let { minAge ->
            patientData.ageYears?.let { age ->
                if (age < minAge) {
                    errors.add(
                        "${drug.name} è controindicata sotto i $minAge anni. " +
                        "Età del paziente: $age anni."
                    )
                }
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}
