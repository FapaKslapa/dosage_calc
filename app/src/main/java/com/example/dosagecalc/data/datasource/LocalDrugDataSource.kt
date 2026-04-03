package com.example.dosagecalc.data.datasource

import android.content.Context
import com.example.dosagecalc.data.model.DrugDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sorgente dati locale: legge e deserializza il file [assets/drugs.json].
 *
 * Responsabilità singola: sa solo come leggere il file JSON dagli asset.
 * Non sa nulla di repository, use case, o UI.
 *
 * @Singleton assicura che il file venga parsato una sola volta durante
 * il ciclo di vita dell'app (lazy initialization tramite by lazy).
 *
 * @param context Context applicativo iniettato da Hilt tramite
 *                @ApplicationContext: non rischia memory leak perché
 *                è il Context dell'Application, non di un Activity.
 */
@Singleton
class LocalDrugDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val DRUGS_FILE_NAME = "drugs.json"
    }

    /**
     * Istanza di Json configurata per essere permissiva con campi sconosciuti:
     * se in futuro il JSON aggiunge nuovi campi non presenti nel DTO,
     * l'app non crasha ma ignora i campi extra.
     * [coerceInputValues] gestisce i null in modo più robusto su valori non-nullable.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * Lista dei farmaci deserializzata una sola volta (lazy).
     * Dopo la prima chiamata, il risultato è in memoria: nessuna I/O aggiuntiva.
     *
     * In un'app di produzione con molti farmaci si sposterebbe la lettura
     * su un Dispatcher.IO, ma per un file di dimensioni contenute
     * il lazy singleton è sufficiente.
     */
    val drugs: List<DrugDto> by lazy {
        readAndParseDrugsJson()
    }

    /**
     * Legge il file JSON dagli asset e lo deserializza in una lista di [DrugDto].
     *
     * @throws IllegalStateException se il file non esiste o il JSON è malformato.
     *         In produzione si gestirebbe con un Result<> e logging,
     *         ma qui preferiamo un fail-fast chiaro durante lo sviluppo.
     */
    private fun readAndParseDrugsJson(): List<DrugDto> {
        // openInputStream sugli asset è thread-safe e restituisce un nuovo stream
        // ad ogni chiamata: usiamo use{} per chiuderlo automaticamente (evita leak)
        val jsonString = context.assets.open(DRUGS_FILE_NAME).use { inputStream ->
            inputStream.bufferedReader().readText()
        }

        return json.decodeFromString<List<DrugDto>>(jsonString)
    }
}
