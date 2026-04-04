package com.example.dosagecalc.domain.repository

import com.example.dosagecalc.domain.model.DrugInteraction
import kotlinx.coroutines.flow.Flow

interface DrugInteractionRepository {
    fun getInteractions(): Flow<List<DrugInteraction>>
    suspend fun checkInteraction(drugId1: String, drugId2: String): DrugInteraction?
}
