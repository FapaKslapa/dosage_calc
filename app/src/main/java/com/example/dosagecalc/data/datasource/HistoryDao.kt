package com.example.dosagecalc.data.datasource

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dosagecalc.data.model.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_records ORDER BY date DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history_records WHERE drugName LIKE '%' || :query || '%' OR patientId IN (SELECT id FROM patients WHERE name LIKE '%' || :query || '%' OR surname LIKE '%' || :query || '%') ORDER BY date DESC")
    fun getAllHistoryPaged(query: String): PagingSource<Int, HistoryEntity>

    @Query("SELECT * FROM history_records WHERE patientId = :patientId ORDER BY date DESC")
    fun getHistoryForPatient(patientId: String): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history_records WHERE patientId = :patientId AND (drugName LIKE '%' || :query || '%') ORDER BY date DESC")
    fun getHistoryForPatientPaged(patientId: String, query: String): PagingSource<Int, HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryRecord(record: HistoryEntity)

    @Query("DELETE FROM history_records WHERE id = :recordId")
    suspend fun deleteHistoryRecord(recordId: String)
}
