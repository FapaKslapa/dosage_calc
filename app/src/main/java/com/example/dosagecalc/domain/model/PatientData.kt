package com.example.dosagecalc.domain.model

/**
 * Modello di dominio puro per i dati antropometrici del paziente.
 *
 * Tutti i campi sono nullable perché non tutti i farmaci richiedono
 * tutti i parametri (es. un farmaco "fixed" non ha bisogno del peso).
 * La validazione di quali campi siano obbligatori per uno specifico
 * farmaco è delegata a [com.example.dosagecalc.domain.usecase.ValidateInputUseCase].
 */
data class PatientData(
    /** Peso corporeo in kg. Richiesto per formule PER_KG e PER_M2. */
    val weightKg: Double?,

    /** Altezza in cm. Richiesta per il calcolo del BSA (formula di Mosteller). */
    val heightCm: Double?,

    /** Età in anni compiuti. Usata per i controlli di sicurezza sull'età minima. */
    val ageYears: Int?
)
