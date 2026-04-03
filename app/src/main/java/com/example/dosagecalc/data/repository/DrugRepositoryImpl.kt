package com.example.dosagecalc.data.repository

import com.example.dosagecalc.data.datasource.LocalDrugDataSource
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.repository.DrugRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrugRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDrugDataSource
) : DrugRepository {

    override fun getDrugs(): Flow<List<Drug>> = flow {
        
        val drugs = localDataSource.drugs.map { dto -> dto.toDomain() }
        emit(drugs)
    }

    override suspend fun getDrugById(id: String): Drug? {
        return localDataSource.drugs
            .firstOrNull { it.id == id }
            ?.toDomain()
    }
}
