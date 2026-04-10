package com.example.dosagecalc.domain.usecase

import com.example.dosagecalc.domain.repository.BsaFormulaType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("CalculateBsaUseCase - Mosteller & Du Bois")
class CalculateBsaUseCaseTest {

    private lateinit var calculateBsa: CalculateBsaUseCase

    @BeforeEach
    fun setUp() {
        calculateBsa = CalculateBsaUseCase()
    }

    @ParameterizedTest(name = "Mosteller: {0} kg / {1} cm -> BSA {2} m²")
    @CsvSource(
        "70.0, 175.0, 1.8447",
        "60.0, 165.0, 1.6583",
        "20.0, 120.0, 0.8165"
    )
    fun `Mosteller BSA calcolato correttamente`(
        weightKg: Double, heightCm: Double, expectedBsa: Double
    ) {
        val result = calculateBsa(weightKg, heightCm, BsaFormulaType.MOSTELLER)
        assertEquals(expectedBsa, result, 0.0001)
    }

    @ParameterizedTest(name = "Du Bois: {0} kg / {1} cm -> BSA {2} m²")
    @CsvSource(
        "70.0, 175.0, 1.8481",
        "60.0, 165.0, 1.6587",
        "20.0, 120.0, 0.8255"
    )
    fun `Du Bois BSA calcolato correttamente`(
        weightKg: Double, heightCm: Double, expectedBsa: Double
    ) {
        val result = calculateBsa(weightKg, heightCm, BsaFormulaType.DU_BOIS)
        assertEquals(expectedBsa, result, 0.0001)
    }

    @Test
    fun `peso zero lancia eccezione`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculateBsa(0.0, 170.0)
        }
    }

    @Test
    fun `altezza negativa lancia eccezione`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculateBsa(70.0, -10.0)
        }
    }
}
