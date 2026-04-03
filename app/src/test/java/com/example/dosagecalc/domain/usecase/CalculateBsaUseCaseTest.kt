package com.example.dosagecalc.domain.usecase

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/**
 * Test unitari per [CalculateBsaUseCase].
 *
 * Questi test NON richiedono Android: girano sulla JVM pura e sono
 * estremamente veloci. Verificano la correttezza matematica della
 * formula di Mosteller con valori di riferimento clinici noti.
 */
@DisplayName("CalculateBsaUseCase - Formula di Mosteller")
class CalculateBsaUseCaseTest {

    private lateinit var calculateBsa: CalculateBsaUseCase

    @BeforeEach
    fun setUp() {
        calculateBsa = CalculateBsaUseCase()
    }

    /**
     * Test parametrizzato con valori di riferimento clinici.
     * Formato CSV: peso_kg, altezza_cm, bsa_atteso
     *
     * Valori verificati manualmente con formula: sqrt(h*w/3600)
     */
    @ParameterizedTest(name = "Paziente {0} kg / {1} cm †’ BSA atteso {2} m")
    @CsvSource(
        "70.0, 175.0, 1.8447",   // adulto maschio tipico
        "60.0, 165.0, 1.6583",   // adulto femmina tipica
        "20.0, 120.0, 0.8165",   // bambino
        "15.0, 100.0, 0.6455"    // bambino piccolo al limite minimo Ivermectina
    )
    fun `BSA calcolato correttamente per valori clinici di riferimento`(
        weightKg: Double, heightCm: Double, expectedBsa: Double
    ) {
        val result = calculateBsa(weightKg, heightCm)
        // Delta di 0.0001 m² per tolleranza di arrotondamento
        assertEquals(expectedBsa, result, 0.0001)
    }

    @Test
    @DisplayName("Peso zero deve lanciare IllegalArgumentException")
    fun `peso zero lancia eccezione`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculateBsa(0.0, 170.0)
        }
    }

    @Test
    @DisplayName("Altezza negativa deve lanciare IllegalArgumentException")
    fun `altezza negativa lancia eccezione`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculateBsa(70.0, -10.0)
        }
    }
}
