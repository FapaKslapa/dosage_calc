package com.example.dosagecalc.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    val isCompleted: Flow<Boolean>
    suspend fun markCompleted()
}
