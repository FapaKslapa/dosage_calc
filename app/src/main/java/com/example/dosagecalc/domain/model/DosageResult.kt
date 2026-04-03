package com.example.dosagecalc.domain.model

/**
 * Risultato del calcolo del dosaggio restituito da [com.example.dosagecalc.domain.usecase.CalculateDosageUseCase].
 *
 * Viene modellato come sealed class per forzare il chiamante (ViewModel)
 * a gestire esplicitamente tutti i casi: successo, errore di validazione,
 * o superamento del tetto di sicurezza. Non è possibile ignorare un caso.
 */
sealed class DosageResult {

    /**
     * Calcolo completato con successo.
     *
     * @param totalDose Dose totale calcolata nel formato grezzo (es. 3600.0 per µg)
     * @param unit      Unità di misura della dose (es. "µg", "mg")
     * @param formula   Stringa leggibile della formula applicata per la tracciabilità
     *                  (es. "200 µg/kg × 18 kg = 3600 µg")
     * @param alert     Avviso clinico del farmaco, da mostrare sempre al professionista
     * @param source    Fonte bibliografica della dose per la tracciabilità
     * @param cappedToMaxDose True se la dose è stata ridotta al massimo consentito per sicurezza
     */
    data class Success(
        val totalDose: Double,
        val unit: String,
        val formula: String,
        val alert: String,
        val source: String,
        val cappedToMaxDose: Boolean = false
    ) : DosageResult()

    /**
     * Errore di validazione: i dati del paziente non soddisfano i requisiti
     * clinici del farmaco (es. peso sotto il minimo, età troppo bassa).
     *
     * @param reason Messaggio leggibile da mostrare all'utente
     */
    data class ValidationError(val reason: String) : DosageResult()

    /**
     * Errore generico inatteso (es. formula non implementata, dati corrotti).
     *
     * @param message Descrizione tecnica dell'errore
     */
    data class Error(val message: String) : DosageResult()
}
