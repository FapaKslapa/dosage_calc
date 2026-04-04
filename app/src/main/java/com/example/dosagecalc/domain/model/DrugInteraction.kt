package com.example.dosagecalc.domain.model

data class DrugInteraction(
    val drugId1: String,
    val drugId2: String,
    val riskLevel: InteractionRiskLevel,
    val description: String
)

enum class InteractionRiskLevel(val label: String) {
    LOW("Lieve"),
    MODERATE("Moderato"),
    HIGH("Grave")
}
