package com.example.dosagecalc.presentation.ui.widget

import android.content.Context
import com.example.dosagecalc.data.AppDatabase
import com.example.dosagecalc.domain.model.HistoryRecord
import com.example.dosagecalc.domain.model.Reminder
import com.example.dosagecalc.domain.model.ReminderInterval
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.Instant

object WidgetDataProvider {

    suspend fun getLastDrug(context: Context): HistoryRecord? {
        val db = AppDatabase.getInstance(context)
        val entities = db.historyDao().getAllHistory().first()
        val lastEntity = entities.firstOrNull() ?: return null
        
        return HistoryRecord(
            id = lastEntity.id,
            patientId = lastEntity.patientId,
            drugId = lastEntity.drugId,
            drugName = lastEntity.drugName,
            date = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastEntity.date), ZoneId.systemDefault()),
            weightKg = lastEntity.weightKg,
            heightCm = lastEntity.heightCm,
            ageYears = lastEntity.ageYears,
            calculatedDose = lastEntity.calculatedDose,
            calculatedDoseMax = lastEntity.calculatedDoseMax,
            doseUnit = lastEntity.doseUnit,
            formulaUsed = lastEntity.formulaUsed,
            notes = lastEntity.notes
        )
    }

    suspend fun getNextReminder(context: Context): Reminder? {
        val db = AppDatabase.getInstance(context)
        val entities = db.reminderDao().getAllReminders().first()
        val reminders = entities.map { e ->
            Reminder(
                id = e.id,
                drugName = e.drugName,
                interval = ReminderInterval.valueOf(e.interval),
                daySelection = e.daySelection,
                hour = e.hour,
                minute = e.minute,
                durationDays = e.durationDays,
                timestamp = e.timestamp
            )
        }
        
        return reminders.firstOrNull()
    }
}
