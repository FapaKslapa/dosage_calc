package com.example.dosagecalc.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.dosagecalc.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : OnboardingRepository {

    private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")

    override val isCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[ONBOARDING_KEY] ?: false
    }

    override suspend fun markCompleted() {
        dataStore.edit { it[ONBOARDING_KEY] = true }
    }
}
