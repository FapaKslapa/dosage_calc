package com.example.dosagecalc.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dosagecalc.data.model.CustomDrugEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomDrugDao {
    @Query("SELECT * FROM custom_drugs")
    fun getAllCustomDrugs(): Flow<List<CustomDrugEntity>>

    @Query("SELECT * FROM custom_drugs WHERE id = :id")
    suspend fun getCustomDrugById(id: String): CustomDrugEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomDrug(drug: CustomDrugEntity)

    @Query("DELETE FROM custom_drugs WHERE id = :id")
    suspend fun deleteCustomDrug(id: String)
}
