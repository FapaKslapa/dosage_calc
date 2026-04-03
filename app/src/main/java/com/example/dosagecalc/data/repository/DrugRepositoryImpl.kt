package com.example.dosagecalc.data.repository

import com.example.dosagecalc.data.datasource.CustomDrugDao
import com.example.dosagecalc.data.datasource.LocalDrugDataSource
import com.example.dosagecalc.data.model.toEntity
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.repository.DrugRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrugRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDrugDataSource,
    private val customDrugDao: CustomDrugDao
) : DrugRepository {

    override fun getDrugs(): Flow<List<Drug>> {
        val staticDrugs = localDataSource.drugs.map { it.toDomain() }

        return customDrugDao.getAllCustomDrugs().map { customEntities ->
            val customDrugs = customEntities.map { it.toDomain() }
            (staticDrugs + customDrugs).sortedBy { it.name }
        }
    }

    override suspend fun getDrugById(id: String): Drug? {
        val staticDrug = localDataSource.drugs.firstOrNull { it.id == id }?.toDomain()
        if (staticDrug != null) return staticDrug

        return customDrugDao.getCustomDrugById(id)?.toDomain()
    }

    override suspend fun addCustomDrug(drug: Drug) {
        customDrugDao.insertCustomDrug(drug.toEntity())
    }

    override suspend fun deleteCustomDrug(id: String) {
        customDrugDao.deleteCustomDrug(id)
    }
}
