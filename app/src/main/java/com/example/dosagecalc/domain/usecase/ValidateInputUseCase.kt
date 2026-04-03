package com.example.dosagecalc.domain.usecase

import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType
import com.example.dosagecalc.domain.model.PatientData
import javax.inject.Inject

/**
 * Use Case: Validazione clinica dei dati del paziente rispetto a un farmaco.
 *
 * Centralizza tutti i controlli di sicurezza pre-calcolo:
 * - Presenza dei campi obbligatori per il tipo di formula
 * - Range fisiologici dei valori (peso, altezza, età)
 * - Limiti clinici specifici del farmaco (peso min/max, età min)
 *
 * Separare la validazione dal calcolo segue il Single Responsibility Principle:
 * [CalculateDosageUseCase] può assumere che i dati siano già stati validati.
 */
class ValidateInputUseCase @Inject constructor() {

    /**
     * Valida i dati del paziente per il farmaco selezionato.
     *
     * @param drug        Farmaco per cui si vuole calcolare la dose.
     * @param patientData Dati antropometrici del paziente.
     * @return            [ValidationResult.Valid] se i dati passano tutti i controlli,
     *                    [ValidationResult.Invalid] con una lista di errori altrimenti.
     */
    operator fun invoke(drug: Drug, patientData: PatientData): ValidationResult {
        val errors = mutableListOf<String>()

        // --- 1. Verifica presenza campi obbligatori in base alla formula ---
        when (drug.formulaType) {
            FormulaType.PER_KG -> {
                if (patientData.weightKg == null) {
                    errors.add("Il peso è obbligatorio per questo farmaco (formula per kg).")
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
            FormulaType.FIXED, FormulaType.BY_RANGE -> {
                // Per dose fissa non serve il peso; per BY_RANGE la validazione
                // è più complessa e delegata all'use case specifico futuro.
            }
        }

        // --- 2. Validazione dei range fisiologici (se i campi sono presenti) ---
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

        // --- 3. Limiti clinici specifici del farmaco ---
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

/**
 * Risultato della validazione: sealed class per garantire
 * la gestione esplicita di entrambi i casi nel chiamante.
 */
sealed class ValidationResult {
    /** Tutti i controlli superati: si può procedere con il calcolo. */
    object Valid : ValidationResult()

    /**
     * Uno o più controlli falliti.
     * @param errors Lista di messaggi di errore leggibili dall'utente.
     */
    data class Invalid(val errors: List<String>) : ValidationResult()
}
