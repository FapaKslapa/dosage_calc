package com.example.dosagecalc.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val isDarkTheme: Flow<Boolean>
    suspend fun setDarkTheme(dark: Boolean)
}
