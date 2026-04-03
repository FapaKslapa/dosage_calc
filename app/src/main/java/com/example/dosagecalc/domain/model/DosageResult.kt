package com.example.dosagecalc.domain.model

sealed class DosageResult {

    data class Success(
        val totalDose: Double,
        val unit: String,
        val formula: String,
        val alert: String,
        val source: String,
        val cappedToMaxDose: Boolean = false
    ) : DosageResult()

    data class ValidationError(val reason: String) : DosageResult()

    data class Error(val message: String) : DosageResult()
}
