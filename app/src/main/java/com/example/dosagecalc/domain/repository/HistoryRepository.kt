package com.example.dosagecalc.domain.repository

import androidx.paging.PagingData
import com.example.dosagecalc.domain.model.HistoryRecord
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<HistoryRecord>>
    fun getAllHistoryPaged(query: String): Flow<PagingData<HistoryRecord>>
    fun getHistoryForPatient(patientId: String): Flow<List<HistoryRecord>>
    fun getHistoryForPatientPaged(patientId: String, query: String): Flow<PagingData<HistoryRecord>>
    suspend fun saveHistoryRecord(record: HistoryRecord)
    suspend fun deleteHistoryRecord(recordId: String)
}
