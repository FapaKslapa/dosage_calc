package com.example.dosagecalc.data.repository

import com.example.dosagecalc.data.datasource.ReminderDao
import com.example.dosagecalc.data.model.toDomain
import com.example.dosagecalc.data.model.toEntity
import com.example.dosagecalc.domain.model.Reminder
import com.example.dosagecalc.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val dao: ReminderDao
) : ReminderRepository {

    override fun getAllReminders(): Flow<List<Reminder>> =
        dao.getAllReminders().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addReminder(reminder: Reminder) {
        dao.insertReminder(reminder.toEntity())
    }

    override suspend fun removeReminder(id: String) {
        dao.deleteReminder(id)
    }
}
