package com.example.dosagecalc.domain.repository

import com.example.dosagecalc.domain.model.Drug
import kotlinx.coroutines.flow.Flow

/**
 * Contratto (interfaccia) del repository nel livello Domain.
 *
 * Il Domain non sa come i dati vengono recuperati (JSON locale, Room,
 * API REST): sa solo che può chiedere una lista di farmaci tramite questa
 * interfaccia. L'implementazione concreta ([com.example.dosagecalc.data.repository.DrugRepositoryImpl])
 * vive nel layer Data e viene iniettata da Hilt.
 *
 * Usiamo Flow<List<Drug>> invece di suspend fun per due motivi:
 * 1. Permette aggiornamenti reattivi in futuro (es. sync con backend).
 * 2. Il ViewModel può collegarsi direttamente allo stream senza logica
 *    di refresh manuale.
 */
interface DrugRepository {

    /**
     * Restituisce lo stream di tutti i farmaci disponibili.
     * Il Flow emette un singolo elemento (la lista completa) all'avvio,
     * ma è pronto per emettere aggiornamenti se la sorgente dati cambia.
     */
    fun getDrugs(): Flow<List<Drug>>

    /**
     * Restituisce un singolo farmaco per ID, o null se non esiste.
     * Usato per recuperare i dettagli dopo che l'utente ha selezionato
     * un farmaco dalla lista.
     */
    suspend fun getDrugById(id: String): Drug?
}
