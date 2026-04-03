package com.example.dosagecalc.domain.usecase

import javax.inject.Inject
import kotlin.math.sqrt

/**
 * Use Case: Calcolo della Superficie Corporea (BSA - Body Surface Area).
 *
 * Implementa la formula di Mosteller (1987), raccomandata da WHO e AIFA
 * per semplicità e accuratezza clinica:
 *
 *   BSA (m²) = √( altezza(cm) × peso(kg) / 3600 )
 *
 * Fonti:
 *  - Mosteller RD. "Simplified Calculation of Body Surface Area."
 *    N Engl J Med. 1987 Oct 22;317(17):1098.
 *  - WHO Technical Report Series, No. 825 (1992).
 *
 * Questa classe non dipende da Android: è testabile con JUnit puro (JVM).
 *
 * @constructor Iniettato da Hilt (javax.inject.Inject).
 */
class CalculateBsaUseCase @Inject constructor() {

    /**
     * Calcola il BSA dato il peso e l'altezza del paziente.
     *
     * @param weightKg  Peso in chilogrammi. Deve essere > 0.
     * @param heightCm  Altezza in centimetri. Deve essere > 0.
     * @return          BSA in m², arrotondato a 4 decimali per precisione clinica.
     * @throws IllegalArgumentException se i valori in input non sono fisiologicamente validi.
     */
    operator fun invoke(weightKg: Double, heightCm: Double): Double {
        // Validazione in ingresso: valori non fisiologici bloccano il calcolo
        // prima di produrre risultati numericamente validi ma clinicamente assurdi.
        require(weightKg > 0) { "Il peso deve essere maggiore di 0 kg, ricevuto: $weightKg" }
        require(heightCm > 0) { "L'altezza deve essere maggiore di 0 cm, ricevuta: $heightCm" }
        require(weightKg <= 500) { "Peso non fisiologico: $weightKg kg" }
        require(heightCm <= 300) { "Altezza non fisiologica: $heightCm cm" }

        // Formula di Mosteller: BSA = sqrt(h_cm * w_kg / 3600)
        val bsa = sqrt((heightCm * weightKg) / 3600.0)

        // Round a 4 decimali: precisione sufficiente per uso clinico
        // (es. 1.7321 m² invece di 1.7320508...)
        return Math.round(bsa * 10_000.0) / 10_000.0
    }
}
