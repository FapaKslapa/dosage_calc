package com.example.dosagecalc.domain.repository
import com.example.dosagecalc.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    suspend fun addReminder(reminder: Reminder)
    suspend fun removeReminder(id: String)
}
