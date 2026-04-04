package com.example.dosagecalc.domain.usecase

import com.example.dosagecalc.domain.model.DrugInteraction
import com.example.dosagecalc.domain.repository.DrugInteractionRepository
import javax.inject.Inject

class CheckDrugInteractionsUseCase @Inject constructor(
    private val repository: DrugInteractionRepository
) {
    suspend operator fun invoke(targetDrugId: String, otherDrugIds: List<String>): List<DrugInteraction> {
        return otherDrugIds.distinct()
            .filter { it != targetDrugId }
            .mapNotNull { otherId -> 
                repository.checkInteraction(targetDrugId, otherId)
            }
    }
}
