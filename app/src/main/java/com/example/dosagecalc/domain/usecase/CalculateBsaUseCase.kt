package com.example.dosagecalc.domain.usecase

import com.example.dosagecalc.domain.repository.BsaFormulaType
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

class CalculateBsaUseCase @Inject constructor() {

    operator fun invoke(weightKg: Double, heightCm: Double, formula: BsaFormulaType = BsaFormulaType.MOSTELLER): Double {

        require(weightKg > 0) { "Il peso deve essere maggiore di 0 kg, ricevuto: $weightKg" }
        require(heightCm > 0) { "L'altezza deve essere maggiore di 0 cm, ricevuta: $heightCm" }
        require(weightKg <= 500) { "Peso non fisiologico: $weightKg kg" }
        require(heightCm <= 300) { "Altezza non fisiologica: $heightCm cm" }

        val bsa = when (formula) {
            BsaFormulaType.MOSTELLER -> {
                sqrt((heightCm * weightKg) / 3600.0)
            }
            BsaFormulaType.DU_BOIS -> {
                0.007184 * heightCm.pow(0.725) * weightKg.pow(0.425)
            }
        }

        return (bsa * 10_000.0).roundToLong() / 10_000.0
    }
}
