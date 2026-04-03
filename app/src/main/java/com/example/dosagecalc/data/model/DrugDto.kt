package com.example.dosagecalc.data.model

import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) per un farmaco letto dal file JSON.
 *
 * Il suffisso "Dto" indica che questo è un oggetto di confine tra il
 * layer Data e il layer Domain. Non deve mai "uscire" dal layer Data:
 * viene immediatamente convertito in [Drug] tramite [toDomain()].
 *
 * @Serializable abilita il parsing automatico da JSON tramite
 * kotlinx.serialization senza reflection runtime (più veloce e sicuro
 * rispetto a Gson/Moshi per Android).
 *
 * @SerialName mappa il nome del campo JSON al nome della proprietà Kotlin.
 * Utile per rispettare le convenzioni JSON (snake_case) pur mantenendo
 * camelCase nel codice Kotlin.
 */
@Serializable
data class DrugDto(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("indication")
    val indication: String,

    /**
     * Tipo di formula come stringa JSON (es. "per_kg").
     * Viene convertito nell'enum [FormulaType] durante il mapping in [toDomain()].
     * Usiamo String nel DTO per isolare il Domain dall'implementazione JSON.
     */
    @SerialName("formulaType")
    val formulaType: String,

    @SerialName("unitDose")
    val unitDose: Double,

    @SerialName("unit")
    val unit: String,

    // I campi nullable usano Double? per allinearsi al JSON (valore null = campo assente)
    @SerialName("minWeightKg")
    val minWeightKg: Double? = null,

    @SerialName("maxWeightKg")
    val maxWeightKg: Double? = null,

    @SerialName("minAgeYears")
    val minAgeYears: Int? = null,

    @SerialName("maxSingleDoseMcg")
    val maxSingleDoseMcg: Double? = null,

    @SerialName("alert")
    val alert: String,

    @SerialName("source")
    val source: String
) {

    /**
     * Mappa questo DTO nel modello di dominio puro [Drug].
     *
     * Questo metodo incapsula tutta la logica di conversione:
     * - Traduce la stringa "formulaType" nell'enum type-safe [FormulaType]
     * - Gestisce valori formulaType sconosciuti (fallback su FIXED con log implicito)
     *
     * Il mapping è una funzione di estensione-membro (extension on self) per
     * mantenere il codice di conversione vicino al DTO senza sporcare il Domain.
     */
    fun toDomain(): Drug = Drug(
        id              = id,
        name            = name,
        indication      = indication,
        formulaType     = when (formulaType.lowercase()) {
            "per_kg"    -> FormulaType.PER_KG
            "per_m2"    -> FormulaType.PER_M2
            "fixed"     -> FormulaType.FIXED
            "by_range"  -> FormulaType.BY_RANGE
            else        -> {
                // Un valore sconosciuto non deve far crashare l'app: usiamo FIXED
                // come fallback sicuro e il problema sarà visibile nel risultato.
                FormulaType.FIXED
            }
        },
        unitDose        = unitDose,
        unit            = unit,
        minWeightKg     = minWeightKg,
        maxWeightKg     = maxWeightKg,
        minAgeYears     = minAgeYears,
        maxSingleDoseMcg = maxSingleDoseMcg,
        alert           = alert,
        source          = source
    )
}
