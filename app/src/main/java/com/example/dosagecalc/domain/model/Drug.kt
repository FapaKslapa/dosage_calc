package com.example.dosagecalc.domain.model

/**
 * Modello di dominio puro per un farmaco.
 *
 * Questo è il cuore del livello Domain: NON dipende da Android, da Compose,
 * né da kotlinx.serialization. È una semplice data class Kotlin che il resto
 * dell'app usa per i calcoli clinici.
 *
 * I dati provengono dal layer Data (DrugDto), che li mappa in questo modello
 * tramite il repository. In questo modo la logica di calcolo non sa nulla
 * di come i dati vengono letti (JSON, database, rete, ecc.).
 */
data class Drug(
    /** Identificatore univoco del farmaco (es. "ivermectina_scabbia") */
    val id: String,

    /** Nome commerciale/generico del farmaco (es. "Ivermectina") */
    val name: String,

    /** Indicazione clinica per cui viene usato (es. "Scabbia") */
    val indication: String,

    /**
     * Tipo di formula di calcolo della dose:
     * - [FormulaType.PER_KG]  → dose = unitDose × peso
     * - [FormulaType.PER_M2]  → dose = unitDose × BSA (superficie corporea)
     * - [FormulaType.FIXED]   → dose fissa indipendente dal paziente
     * - [FormulaType.BY_RANGE]→ dose determinata da fasce (es. peso/età)
     */
    val formulaType: FormulaType,

    /** Dose unitaria di riferimento (es. 200 per Ivermectina 200 µg/kg) */
    val unitDose: Double,

    /** Unità di misura della dose (es. "µg", "mg", "applicazione") */
    val unit: String,

    /** Peso minimo del paziente in kg sotto cui il farmaco è controindicato (nullable) */
    val minWeightKg: Double?,

    /** Peso massimo del paziente in kg (nullable se non esiste un tetto) */
    val maxWeightKg: Double?,

    /** Età minima in anni (nullable se non c'è restrizione) */
    val minAgeYears: Int?,

    /**
     * Dose massima assoluta per singola somministrazione in µg.
     * Serve come "ceiling" di sicurezza: anche se il calcolo per peso
     * darebbe di più, non si supera questo valore.
     * Nullable per farmaci topici/dose fissa senza tetto definito.
     */
    val maxSingleDoseMcg: Double?,

    /** Avvisi clinici da mostrare al professionista sanitario */
    val alert: String,

    /**
     * Fonte bibliografica della dose (es. "WHO Model Formulary 2023").
     * Essenziale per la tracciabilità clinica: il medico deve poter
     * verificare la fonte del calcolo.
     */
    val source: String
)

/**
 * Tipo di formula di calcolo della dose.
 * Sealed class sarebbe più potente, ma enum è sufficiente come punto
 * di partenza: aggiungere [BY_RANGE] in futuro richiederà solo un nuovo
 * caso e un nuovo use case di calcolo.
 */
enum class FormulaType {
    /** Dose proporzionale al peso corporeo (mg/kg o µg/kg) */
    PER_KG,

    /** Dose proporzionale alla superficie corporea in m² (tipico chemioterapici) */
    PER_M2,

    /** Dose fissa, indipendente dal peso o dall'età */
    FIXED,

    /** Dose determinata da fasce di peso/età definite nel JSON */
    BY_RANGE
}
