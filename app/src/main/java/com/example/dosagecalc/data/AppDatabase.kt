package com.example.dosagecalc.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dosagecalc.data.datasource.HistoryDao
import com.example.dosagecalc.data.datasource.PatientDao
import com.example.dosagecalc.data.model.HistoryEntity
import com.example.dosagecalc.data.model.PatientEntity

@Database(
    entities = [PatientEntity::class, HistoryEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun historyDao(): HistoryDao
}
