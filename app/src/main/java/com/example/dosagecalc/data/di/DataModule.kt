package com.example.dosagecalc.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.dosagecalc.data.AppDatabase
import com.example.dosagecalc.data.datasource.CustomDrugDao
import com.example.dosagecalc.data.datasource.HistoryDao
import com.example.dosagecalc.data.datasource.PatientDao
import com.example.dosagecalc.data.datasource.ReminderDao
import com.example.dosagecalc.data.repository.DrugRepositoryImpl
import com.example.dosagecalc.data.repository.HistoryRepositoryImpl
import com.example.dosagecalc.data.repository.OnboardingRepositoryImpl
import com.example.dosagecalc.data.repository.PatientRepositoryImpl
import com.example.dosagecalc.data.repository.ReminderRepositoryImpl
import com.example.dosagecalc.data.repository.ThemeRepositoryImpl
import com.example.dosagecalc.domain.repository.DrugRepository
import com.example.dosagecalc.domain.repository.HistoryRepository
import com.example.dosagecalc.domain.repository.OnboardingRepository
import com.example.dosagecalc.domain.repository.PatientRepository
import com.example.dosagecalc.domain.repository.ReminderRepository
import com.example.dosagecalc.domain.repository.ThemeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton abstract fun bindDrugRepository(impl: DrugRepositoryImpl): DrugRepository
    @Binds @Singleton abstract fun bindThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository
    @Binds @Singleton abstract fun bindOnboardingRepository(impl: OnboardingRepositoryImpl): OnboardingRepository

    @Module
    @InstallIn(SingletonComponent::class)
    object ProviderModule {
        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.dataStore

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "dosagecalc_database"
            // Versions 1-5 were pre-release installs; destructive wipe is acceptable.
            // Any future schema bump (v7+) must provide a proper Migration object.
            ).fallbackToDestructiveMigrationFrom(1, 2, 3, 4, 5, 6, 7).build()
        }

        @Provides
        fun providePatientDao(database: AppDatabase): PatientDao {
            return database.patientDao()
        }

        @Provides
        fun provideHistoryDao(database: AppDatabase): HistoryDao {
            return database.historyDao()
        }

        @Provides
        @Singleton
        fun providePatientRepository(patientDao: PatientDao): PatientRepository {
            return PatientRepositoryImpl(patientDao)
        }

        @Provides
        @Singleton
        fun provideHistoryRepository(historyDao: HistoryDao): HistoryRepository {
            return HistoryRepositoryImpl(historyDao)
        }

        @Provides
        fun provideReminderDao(database: AppDatabase): ReminderDao {
            return database.reminderDao()
        }

        @Provides
        fun provideCustomDrugDao(database: AppDatabase): CustomDrugDao {
            return database.customDrugDao()
        }

        @Provides
        @Singleton
        fun provideReminderRepository(reminderDao: ReminderDao): ReminderRepository {
            return ReminderRepositoryImpl(reminderDao)
        }
    }
}

