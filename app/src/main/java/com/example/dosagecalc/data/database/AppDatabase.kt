package com.example.dosagecalc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dosagecalc.data.datasource.CustomDrugDao
import com.example.dosagecalc.data.datasource.HistoryDao
import com.example.dosagecalc.data.datasource.PatientDao
import com.example.dosagecalc.data.datasource.ReminderDao
import com.example.dosagecalc.data.model.CustomDrugEntity
import com.example.dosagecalc.data.model.HistoryEntity
import com.example.dosagecalc.data.model.PatientEntity
import com.example.dosagecalc.data.model.ReminderEntity
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [PatientEntity::class, HistoryEntity::class, ReminderEntity::class, CustomDrugEntity::class],
    version = 8,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun historyDao(): HistoryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun customDrugDao(): CustomDrugDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = "dosage-calc-secure-key".toByteArray()
                val factory = SupportOpenHelperFactory(passphrase)
                
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dosagecalc_secure_db"
                )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
