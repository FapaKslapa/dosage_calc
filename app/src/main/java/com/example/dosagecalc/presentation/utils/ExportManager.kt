package com.example.dosagecalc.presentation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.dosagecalc.domain.model.HistoryRecord
import com.example.dosagecalc.domain.model.Patient
import kotlinx.serialization.json.Json
import java.io.File
import java.time.format.DateTimeFormatter

class ExportManager(private val context: Context) {

    private val json = Json { prettyPrint = true }

    fun exportPatientsToJson(patients: List<Patient>) {
        val jsonString = json.encodeToString(patients.map { p ->
            mapOf(
                "id" to p.id,
                "nome" to p.name,
                "cognome" to p.surname,
                "peso" to p.weightKg.toString(),
                "altezza" to p.heightCm.toString(),
                "eta" to p.ageYears.toString(),
                "note" to (p.notes ?: "")
            )
        })
        
        shareFile(jsonString, "pazienti_backup.json", "application/json")
    }

    fun exportHistoryToCsv(records: List<HistoryRecord>) {
        val header = "Data,Farmaco,Paziente,Peso(kg),Dose Calcolata,Unita,Formula\n"
        val csvString = StringBuilder(header)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        records.forEach { r ->
            csvString.append("${r.date.format(formatter)},")
            csvString.append("${r.drugName.replace(",", " ")},")
            csvString.append("${if (r.patientId != null) "ID:"+r.patientId else "Anonimo"},")
            csvString.append("${r.weightKg},")
            csvString.append("${r.calculatedDose},")
            csvString.append("${r.doseUnit},")
            csvString.append("${r.formulaUsed?.replace(",", ";") ?: ""}\n")
        }

        shareFile(csvString.toString(), "storico_calcoli.csv", "text/csv")
    }

    private fun shareFile(content: String, fileName: String, mimeType: String) {
        val file = File(context.cacheDir, fileName)
        file.writeText(content)

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Esporta file"))
    }
}
