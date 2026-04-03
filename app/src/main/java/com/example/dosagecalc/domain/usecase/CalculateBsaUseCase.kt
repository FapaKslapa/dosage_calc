package com.example.dosagecalc.domain.usecase

import javax.inject.Inject
import kotlin.math.roundToLong
import kotlin.math.sqrt

class CalculateBsaUseCase @Inject constructor() {

    operator fun invoke(weightKg: Double, heightCm: Double): Double {

        require(weightKg > 0) { "Il peso deve essere maggiore di 0 kg, ricevuto: $weightKg" }
        require(heightCm > 0) { "L'altezza deve essere maggiore di 0 cm, ricevuta: $heightCm" }
        require(weightKg <= 500) { "Peso non fisiologico: $weightKg kg" }
        require(heightCm <= 300) { "Altezza non fisiologica: $heightCm cm" }

        // Mosteller formula: BSA = sqrt((height * weight) / 3600)
        val bsa = sqrt((heightCm * weightKg) / 3600.0)

        return (bsa * 10_000.0).roundToLong() / 10_000.0
    }
}
