package com.example.dosagecalc.domain.usecase

import com.example.dosagecalc.domain.model.DrugInteraction
import com.example.dosagecalc.domain.model.InteractionRiskLevel
import com.example.dosagecalc.domain.repository.DrugInteractionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CheckDrugInteractionsUseCaseTest {

    private lateinit var repository: DrugInteractionRepository
    private lateinit var checkInteractions: CheckDrugInteractionsUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        checkInteractions = CheckDrugInteractionsUseCase(repository)
    }

    @Test
    fun `ritorna lista interazioni se presenti`() = runBlocking {
        val target = "drugA"
        val others = listOf("drugB", "drugC")
        
        val interactionAB = DrugInteraction(target, "drugB", InteractionRiskLevel.HIGH, "Pericolo")
        
        coEvery { repository.checkInteraction(target, "drugB") } returns interactionAB
        coEvery { repository.checkInteraction(target, "drugC") } returns null

        val result = checkInteractions(target, others)

        assertEquals(1, result.size)
        assertEquals("drugB", result[0].drugId2)
        assertEquals(InteractionRiskLevel.HIGH, result[0].riskLevel)
    }

    @Test
    fun `ritorna lista vuota se nessuna interazione`() = runBlocking {
        val target = "drugA"
        val others = listOf("drugB")
        
        coEvery { repository.checkInteraction(any(), any()) } returns null

        val result = checkInteractions(target, others)

        assertEquals(0, result.size)
    }
}
