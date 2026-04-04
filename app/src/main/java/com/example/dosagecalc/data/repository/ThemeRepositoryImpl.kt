package com.example.dosagecalc.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.dosagecalc.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {

    private val IS_DARK_KEY = booleanPreferencesKey("is_dark_theme")

    override val isDarkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_DARK_KEY] ?: true   // default: dark
    }

    override suspend fun setDarkTheme(dark: Boolean) {
        dataStore.edit { it[IS_DARK_KEY] = dark }
    }
}
