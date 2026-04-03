package com.example.dosagecalc.domain.repository

import com.example.dosagecalc.domain.model.Drug
import kotlinx.coroutines.flow.Flow

interface DrugRepository {

    fun getDrugs(): Flow<List<Drug>>

    suspend fun getDrugById(id: String): Drug?
}
