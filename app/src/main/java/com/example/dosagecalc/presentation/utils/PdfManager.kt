package com.example.dosagecalc.presentation.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.Patient
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object PdfManager {

    fun generateAndSharePdf(
        context: Context,
        drug: Drug,
        patient: Patient?,
        result: DosageResult.Success
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72 PPI
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldPaint = Paint(paint).apply {
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }

        val titlePaint = Paint(boldPaint).apply {
            textSize = 22f
        }

        var y = 50f
        val x = 50f

        canvas.drawText("REFERTO DOSAGGIO - DosageCalc", x, y, titlePaint)
        y += 40f

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy - HH:mm", Locale.getDefault())
        canvas.drawText("Data: ${LocalDateTime.now().format(formatter)}", x, y, paint)
        y += 40f

        canvas.drawText("PAZIENTE:", x, y, boldPaint)
        y += 20f
        if (patient != null) {
            canvas.drawText("Nome: ${patient.name} ${patient.surname}", x + 20f, y, paint)
            y += 20f
            canvas.drawText("Peso: ${patient.weightKg} kg", x + 20f, y, paint)
            y += 20f
            if (patient.heightCm != null) {
                canvas.drawText("Altezza: ${patient.heightCm} cm", x + 20f, y, paint)
                y += 20f
            }
            canvas.drawText("Età: ${patient.ageYears} anni", x + 20f, y, paint)
            y += 30f
        } else {
            canvas.drawText("Dati non salvati (Calcolo anonimo)", x + 20f, y, paint)
            y += 30f
        }

        canvas.drawText("FARMACO:", x, y, boldPaint)
        y += 20f
        canvas.drawText("Nome: ${drug.name}", x + 20f, y, paint)
        y += 20f
        canvas.drawText("Indicazione: ${drug.indication}", x + 20f, y, paint)
        y += 30f

        canvas.drawText("DOSAGGIO CALCOLATO:", x, y, boldPaint)
        y += 20f

        val doseRangeStr = if (result.totalDoseMax != null) {
            "${String.format(Locale.getDefault(), "%.2f", result.totalDose)} - ${String.format(Locale.getDefault(), "%.2f", result.totalDoseMax)} ${result.unit}"
        } else {
            "${String.format(Locale.getDefault(), "%.2f", result.totalDose)} ${result.unit}"
        }

        canvas.drawText(doseRangeStr, x + 20f, y, titlePaint)
        y += 40f

        canvas.drawText("Formula utilizzata:", x + 20f, y, boldPaint)
        y += 20f

        // Handle long formula text spanning multiple lines
        val lines = result.formula.chunked(60)
        for (line in lines) {
            canvas.drawText(line, x + 20f, y, paint)
            y += 20f
        }
        y += 10f

        if (result.cappedToMaxDose) {
            val alertPaint = Paint(boldPaint).apply { color = Color.RED }
            canvas.drawText("⚠ ATTENZIONE: La dose è stata limitata al massimo consentito.", x + 20f, y, alertPaint)
            y += 30f
        }
        if (drug.alert.isNotBlank()) {
            canvas.drawText("Avviso clinico:", x + 20f, y, boldPaint)
            y += 20f
            val alertLines = drug.alert.chunked(60)
            for (line in alertLines) {
                canvas.drawText(line, x + 20f, y, paint)
                y += 20f
            }
            y += 20f
        }

        paint.textSize = 10f
        y = 750f
        canvas.drawText("DISCLAIMER: Strumento a uso esclusivamente didattico. Verificare sempre il dosaggio su fonti ufficiali (RCP).", x, y, paint)

        document.finishPage(page)

        try {
            val file = File(context.cacheDir, "Referto_${drug.name}_${System.currentTimeMillis()}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                putExtra(android.content.Intent.EXTRA_SUBJECT, "Referto DosageCalc: ${drug.name}")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(android.content.Intent.createChooser(intent, "Condividi il referto..."))

        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
        }
    }
}
