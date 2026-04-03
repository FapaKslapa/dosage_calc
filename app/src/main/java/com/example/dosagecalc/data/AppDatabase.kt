package com.example.dosagecalc.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dosagecalc.data.datasource.HistoryDao
import com.example.dosagecalc.data.datasource.PatientDao
import com.example.dosagecalc.data.datasource.ReminderDao
import com.example.dosagecalc.data.model.HistoryEntity
import com.example.dosagecalc.data.model.PatientEntity
import com.example.dosagecalc.data.model.ReminderEntity

@Database(
    entities = [PatientEntity::class, HistoryEntity::class, ReminderEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun historyDao(): HistoryDao
    abstract fun reminderDao(): ReminderDao
}
