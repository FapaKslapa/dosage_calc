package com.example.dosagecalc.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dosagecalc.domain.model.ReminderInterval
import com.example.dosagecalc.domain.model.Reminder
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val drugName: String,
    val interval: String,
    val daySelection: Int,
    val hour: Int,
    val minute: Int,
    val durationDays: Int,
    val timestamp: Long
)
fun ReminderEntity.toDomain() = Reminder(
    id = id,
    drugName = drugName,
    interval = ReminderInterval.valueOf(interval),
    daySelection = daySelection,
    hour = hour,
    minute = minute,
    durationDays = durationDays,
    timestamp = timestamp
)
fun Reminder.toEntity() = ReminderEntity(
    id = id,
    drugName = drugName,
    interval = interval.name,
    daySelection = daySelection,
    hour = hour,
    minute = minute,
    durationDays = durationDays,
    timestamp = timestamp
)
