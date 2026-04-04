package com.example.dosagecalc.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val isDarkTheme: Flow<Boolean>
    suspend fun setDarkTheme(dark: Boolean)

    val bsaFormula: Flow<BsaFormulaType>
    suspend fun setBsaFormula(formula: BsaFormulaType)
}

enum class BsaFormulaType {
    MOSTELLER, // BSA = sqrt((cm * kg) / 3600)
    DU_BOIS    // BSA = 0.007184 * (cm^0.725) * (kg^0.425)
}
