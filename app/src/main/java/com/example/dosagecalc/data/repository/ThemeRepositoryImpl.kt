package com.example.dosagecalc.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dosagecalc.domain.repository.BsaFormulaType
import com.example.dosagecalc.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {

    private val IS_DARK_KEY = booleanPreferencesKey("is_dark_theme")
    private val BSA_FORMULA_KEY = stringPreferencesKey("bsa_formula")

    override val isDarkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_DARK_KEY] ?: false
    }

    override suspend fun setDarkTheme(dark: Boolean) {
        dataStore.edit { it[IS_DARK_KEY] = dark }
    }

    override val bsaFormula: Flow<BsaFormulaType> = dataStore.data.map { prefs ->
        val name = prefs[BSA_FORMULA_KEY] ?: BsaFormulaType.MOSTELLER.name
        try { BsaFormulaType.valueOf(name) } catch (e: Exception) { BsaFormulaType.MOSTELLER }
    }

    override suspend fun setBsaFormula(formula: BsaFormulaType) {
        dataStore.edit { it[BSA_FORMULA_KEY] = formula.name }
    }
}
