package com.example.dosagecalc.domain.usecase

import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType
import com.example.dosagecalc.domain.model.PatientData
import javax.inject.Inject

/**
 * Use Case: Calcolo della dose totale per un paziente specifico.
 *
 * Coordina la validazione e lo smistamento del calcolo al motore corretto
 * in base al [FormulaType] del farmaco. Questo use case è il punto di ingresso
 * principale per la logica clinica.
 *
 * Flusso:
 * 1. Validazione dei dati tramite [ValidateInputUseCase]
 * 2. Calcolo della dose grezza in base al tipo di formula
 * 3. Applicazione del "ceiling" (dose massima assoluta di sicurezza)
 * 4. Costruzione della stringa formula per la tracciabilità
 *
 * @param validateInputUseCase Use case per la validazione pre-calcolo.
 * @param calculateBsaUseCase  Use case per il calcolo del BSA (superficie corporea).
 */
class CalculateDosageUseCase @Inject constructor(
    private val validateInputUseCase: ValidateInputUseCase,
    private val calculateBsaUseCase: CalculateBsaUseCase
) {

    /**
     * Calcola la dose totale per il paziente.
     *
     * @param drug        Farmaco selezionato (con tutte le sue regole cliniche).
     * @param patientData Dati antropometrici del paziente.
     * @return            [DosageResult] che modella successo, errore di validazione
     *                    o errore inatteso.
     */
    operator fun invoke(drug: Drug, patientData: PatientData): DosageResult {

        // --- Passo 1: Validazione ---
        val validation = validateInputUseCase(drug, patientData)
        if (validation is ValidationResult.Invalid) {
            // Aggrega tutti gli errori in un unico messaggio leggibile
            return DosageResult.ValidationError(
                reason = validation.errors.joinToString(separator = "\n• ", prefix = "• ")
            )
        }

        // --- Passo 2: Calcolo in base al tipo di formula ---
        return when (drug.formulaType) {

            FormulaType.PER_KG -> calculatePerKg(drug, patientData)

            FormulaType.PER_M2 -> calculatePerM2(drug, patientData)

            FormulaType.FIXED -> calculateFixed(drug)

            FormulaType.BY_RANGE -> {
                // Non ancora implementato: restituiamo un errore esplicito
                // invece di un crash silenzioso.
                DosageResult.Error(
                    "La formula BY_RANGE non è ancora supportata per ${drug.name}."
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // Motori di calcolo privati
    // -------------------------------------------------------------------------

    /** Dose = unitDose × peso_kg (es. Ivermectina 200 µg/kg) */
    private fun calculatePerKg(drug: Drug, patientData: PatientData): DosageResult {
        val weight = patientData.weightKg!!  // Non-null garantita dalla validazione
        val rawDose = drug.unitDose * weight

        // Controlla se supera il tetto di sicurezza
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

    /** Dose = unitDose × BSA_m2 (usata tipicamente per chemioterapici dermatologici) */
    private fun calculatePerM2(drug: Drug, patientData: PatientData): DosageResult {
        val weight = patientData.weightKg!!   // Non-null garantita dalla validazione
        val height = patientData.heightCm!!   // Non-null garantita dalla validazione

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

    /** Dose fissa: indipendente dal paziente */
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

    /**
     * Applica il "dose ceiling": se la dose calcolata supera il massimo
     * assoluto consentito, la tronca al massimo e segnala il capping.
     *
     * @param dose    Dose grezza calcolata.
     * @param maxDose Dose massima consentita (null = nessun tetto).
     * @return        Pair<dose_finale, cappedFlag>.
     */
    private fun applyCeiling(dose: Double, maxDose: Double?): Pair<Double, Boolean> {
        if (maxDose == null || dose <= maxDose) return Pair(dose, false)
        return Pair(maxDose, true)
    }
}
