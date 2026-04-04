package com.example.dosagecalc.presentation.calculator

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dosagecalc.domain.model.Reminder
import com.example.dosagecalc.domain.repository.ReminderRepository
import com.example.dosagecalc.presentation.ui.widget.NextReminderWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val repository: ReminderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val reminders: StateFlow<List<Reminder>> = repository.getAllReminders()
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch { 
            repository.addReminder(reminder) 
            updateWidget()
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch { 
            repository.removeReminder(id) 
            updateWidget()
        }
    }

    private fun updateWidget() {
        viewModelScope.launch {
            val manager = GlanceAppWidgetManager(context)
            manager.getGlanceIds(NextReminderWidget::class.java).forEach { id ->
                NextReminderWidget().update(context, id)
            }
        }
    }
}
