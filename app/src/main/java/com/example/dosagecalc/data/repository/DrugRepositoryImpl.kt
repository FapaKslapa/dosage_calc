package com.example.dosagecalc.data.repository

import com.example.dosagecalc.data.datasource.LocalDrugDataSource
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.repository.DrugRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementazione concreta di [DrugRepository].
 *
 * Vive nel layer Data e implementa il contratto definito nel Domain.
 * Il Domain non conosce questa classe: la vede solo come [DrugRepository]
 * grazie all'iniezione di Hilt configurata in [DataModule].
 *
 * Attualmente delega tutta la lettura a [LocalDrugDataSource] (file JSON).
 * In futuro si potrebbe aggiungere una sorgente Room o un'API REST senza
 * cambiare una sola riga del Domain o della Presentation.
 *
 * @param localDataSource Sorgente dati locale JSON, iniettata da Hilt.
 */
@Singleton
class DrugRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDrugDataSource
) : DrugRepository {

    /**
     * Restituisce lo stream dei farmaci come Flow.
     *
     * Usiamo flow { emit(...) } invece di flowOf() per isolare l'operazione
     * di lettura dal thread del chiamante e per permettere future trasformazioni
     * (es. merge con una sorgente remota).
     */
    override fun getDrugs(): Flow<List<Drug>> = flow {
        // Mappa ogni DTO nel modello di dominio puro prima di emetterlo
        val drugs = localDataSource.drugs.map { dto -> dto.toDomain() }
        emit(drugs)
    }

    /**
     * Restituisce un farmaco per ID cercandolo nella lista in memoria.
     * Complessità O(n) accettabile per un dataset piccolo (<100 farmaci).
     */
    override suspend fun getDrugById(id: String): Drug? {
        return localDataSource.drugs
            .firstOrNull { it.id == id }
            ?.toDomain()
    }
}
