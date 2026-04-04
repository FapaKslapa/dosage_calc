package com.example.dosagecalc.domain.model

enum class ReminderInterval(val label: String) {
    DAILY("Giornaliero"),
    WEEKLY("Settimanale"),
    MONTHLY("Mensile")
}

data class Reminder(
    val id: String,
    val drugName: String,
    val interval: ReminderInterval,
    val daySelection: Int, // 0 for daily, 1-7 for weekly, 1-31 for monthly
    val hour: Int,
    val minute: Int,
    val durationDays: Int,
    val timestamp: Long = System.currentTimeMillis()
)

