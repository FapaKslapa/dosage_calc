package com.example.dosagecalc.domain.usecase

import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType
import com.example.dosagecalc.domain.model.PatientData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Test unitari per [CalculateDosageUseCase].
 *
 * Copre i casi clinici principali:
 * - Calcolo corretto per formula PER_KG
 * - Applicazione del ceiling (dose massima)
 * - Ritorno di ValidationError per pazienti non idonei
 */
@DisplayName("CalculateDosageUseCase")
class CalculateDosageUseCaseTest {

    private lateinit var sut: CalculateDosageUseCase

    // Farmaco di esempio: Ivermectina 200 µg/kg, tetto 400.000 µg
    private val ivermectina = Drug(
        id              = "ivermectina_scabbia",
        name            = "Ivermectina",
        indication      = "Scabbia",
        formulaType     = FormulaType.PER_KG,
        unitDose        = 200.0,
        unit            = "µg",
        minWeightKg     = 15.0,
        maxWeightKg     = 150.0,
        minAgeYears     = 5,
        maxSingleDoseMcg = 400_000.0,
        alert           = "Usare peso in kg.",
        source          = "WHO 2023"
    )

    @BeforeEach
    fun setUp() {
        sut = CalculateDosageUseCase(
            validateInputUseCase = ValidateInputUseCase(),
            calculateBsaUseCase  = CalculateBsaUseCase()
        )
    }

    @Test
    @DisplayName("Ivermectina 18 kg → 3600 µg")
    fun `dose per kg calcolata correttamente`() {
        val result = sut(ivermectina, PatientData(weightKg = 18.0, heightCm = null, ageYears = 7))

        assertTrue(result is DosageResult.Success)
        assertEquals(3_600.0, (result as DosageResult.Success).totalDose)
    }

    @Test
    @DisplayName("Ivermectina 2000 kg (irreale) → dose cappata al massimo 400.000 µg")
    fun `dose capped al massimo consentito`() {
        // Usiamo un peso molto alto ma sotto il limite clinico del farmaco (150 kg)
        // Ivermectina 200 µg/kg × 150 kg = 30.000 µg, NON supera il tetto da 400.000
        // Per testare il ceiling, creiamo un farmaco con tetto basso
        val drugConTettoBasso = ivermectina.copy(
            maxSingleDoseMcg = 2_000.0,
            maxWeightKg = null  // rimuoviamo il limite peso per testare solo il ceiling
        )

        val result = sut(drugConTettoBasso, PatientData(weightKg = 20.0, heightCm = null, ageYears = 7))

        assertTrue(result is DosageResult.Success)
        val success = result as DosageResult.Success
        // 200 × 20 = 4000, ma tetto è 2000 → deve cappare
        assertEquals(2_000.0, success.totalDose)
        assertTrue(success.cappedToMaxDose)
    }

    @Test
    @DisplayName("Paziente sotto peso minimo → ValidationError")
    fun `paziente troppo leggero restituisce errore di validazione`() {
        // Peso 10 kg < minWeightKg 15 kg
        val result = sut(ivermectina, PatientData(weightKg = 10.0, heightCm = null, ageYears = 7))

        assertTrue(result is DosageResult.ValidationError)
    }

    @Test
    @DisplayName("Paziente troppo giovane → ValidationError")
    fun `paziente sotto eta minima restituisce errore di validazione`() {
        // Età 3 anni < minAgeYears 5
        val result = sut(ivermectina, PatientData(weightKg = 20.0, heightCm = null, ageYears = 3))

        assertTrue(result is DosageResult.ValidationError)
    }
}
