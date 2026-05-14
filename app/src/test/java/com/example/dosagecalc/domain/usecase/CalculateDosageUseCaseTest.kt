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

@DisplayName("CalculateDosageUseCase")
class CalculateDosageUseCaseTest {
    private lateinit var sut: CalculateDosageUseCase

    private val ivermectina =
        Drug(
            id = "ivermectina_scabbia",
            name = "Ivermectina",
            indication = "Scabbia",
            formulaType = FormulaType.PER_KG,
            unitDose = 200.0,
            unit = "µg",
            minWeightKg = 15.0,
            maxWeightKg = 150.0,
            minAgeYears = 5,
            maxSingleDoseMcg = 400_000.0,
            alert = "Usare peso in kg.",
            source = "WHO 2023",
        )

    @BeforeEach
    fun setUp() {
        sut =
            CalculateDosageUseCase(
                validateInputUseCase = ValidateInputUseCase(),
                calculateBsaUseCase = CalculateBsaUseCase(),
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
        val drugConTettoBasso =
            ivermectina.copy(
                maxSingleDoseMcg = 2_000.0,
                maxWeightKg = null,
            )

        val result = sut(drugConTettoBasso, PatientData(weightKg = 20.0, heightCm = null, ageYears = 7))

        assertTrue(result is DosageResult.Success)
        val success = result as DosageResult.Success
        assertEquals(2_000.0, success.totalDose)
        assertTrue(success.cappedToMaxDose)
    }

    @Test
    @DisplayName("Paziente sotto peso minimo → ValidationError")
    fun `paziente troppo leggero restituisce errore di validazione`() {
        val result = sut(ivermectina, PatientData(weightKg = 10.0, heightCm = null, ageYears = 7))

        assertTrue(result is DosageResult.ValidationError)
    }

    @Test
    @DisplayName("Paziente troppo giovane → ValidationError")
    fun `paziente sotto eta minima restituisce errore di validazione`() {
        val result = sut(ivermectina, PatientData(weightKg = 20.0, heightCm = null, ageYears = 3))

        assertTrue(result is DosageResult.ValidationError)
    }
}
