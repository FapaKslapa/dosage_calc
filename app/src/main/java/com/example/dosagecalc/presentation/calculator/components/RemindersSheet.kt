package com.example.dosagecalc.presentation.calculator.components
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import com.example.dosagecalc.presentation.ui.util.isCompactHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.Reminder
import com.example.dosagecalc.domain.model.ReminderInterval
import com.example.dosagecalc.presentation.calculator.RemindersViewModel
import com.example.dosagecalc.presentation.utils.ReminderManager
import java.util.UUID

@Composable
fun RemindersSheet(
    context: Context,
    drugName: String?,
    onDismissRequest: () -> Unit,
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val isCompact = isCompactHeight()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Promemoria Attivi",
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (reminders.isEmpty()) {
                Text(
                    text = "Nessun promemoria attualmente impostato.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(reminders) { reminder ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = reminder.drugName, style = MaterialTheme.typography.titleMedium)
                                    val intervalText = when (reminder.interval) {
                                        ReminderInterval.DAILY -> "Ogni giorno"
                                        ReminderInterval.WEEKLY -> "Ogni settimana (Giorno )"
                                        ReminderInterval.MONTHLY -> "Ogni mese (Giorno )"
                                    }
                                    val timeText = ":"
                                    Text(
                                        text = " alle ",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                IconButton(onClick = {
                                    ReminderManager.cancelReminderSeries(context, reminder.id)
                                    viewModel.deleteReminder(reminder.id)
                                    Toast.makeText(context, "Promemoria eliminato", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Elimina", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Crea Nuovo Promemoria",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (drugName == null) {
                Text("Esegui prima un calcolo per un farmaco.", modifier = Modifier.padding(vertical = 16.dp))
            } else {
                var selectedInterval by remember { mutableStateOf(ReminderInterval.DAILY) }
                val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)
                var durationDays by remember { mutableStateOf("5") }
                var daySelection by remember { mutableStateOf("1") }

                Text(
                    text = "Frequenza",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ReminderInterval.values().forEachIndexed { index, interval ->
                        SegmentedButton(
                            selected = selectedInterval == interval,
                            onClick = { selectedInterval = interval },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = ReminderInterval.values().size),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(
                                text = when (interval) {
                                    ReminderInterval.DAILY -> "Giornaliero"
                                    ReminderInterval.WEEKLY -> "Settimanale"
                                    ReminderInterval.MONTHLY -> "Mensile"
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Orario della somministrazione",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                val timePickerColors = TimePickerDefaults.colors(
                    clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    selectorColor = MaterialTheme.colorScheme.primary,
                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (isCompact) {
                        TimeInput(state = timePickerState, colors = timePickerColors)
                    } else {
                        TimePicker(state = timePickerState, colors = timePickerColors)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = durationDays,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) durationDays = it },
                        label = {
                            Text(when(selectedInterval) {
                                ReminderInterval.DAILY -> "Durata (Giorni)"
                                ReminderInterval.WEEKLY -> "Durata (Settimane)"
                                ReminderInterval.MONTHLY -> "Durata (Mesi)"
                            })
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    if (selectedInterval == ReminderInterval.MONTHLY) {
                        OutlinedTextField(
                            value = daySelection,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) daySelection = it },
                            label = { Text("Giorno (1-31)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                if (selectedInterval == ReminderInterval.WEEKLY) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Giorno della settimana",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val daysOfWeek = listOf("L", "M", "M", "G", "V", "S", "D")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        daysOfWeek.forEachIndexed { index, day ->
                            val dayNumber = index + 1
                            val isSelected = (daySelection.toIntOrNull() ?: 1) == dayNumber
                            FilterChip(
                                selected = isSelected,
                                onClick = { daySelection = dayNumber.toString() },
                                label = { Text(day, style = MaterialTheme.typography.bodyMedium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val seriesId = UUID.randomUUID().toString()
                        val dur = durationDays.toIntOrNull() ?: 1
                        val ds = daySelection.toIntOrNull() ?: 1

                        val reminder = Reminder(
                            id = seriesId,
                            drugName = drugName,
                            interval = selectedInterval,
                            daySelection = if (selectedInterval == ReminderInterval.DAILY) 0 else ds,
                            hour = timePickerState.hour,
                            minute = timePickerState.minute,
                            durationDays = dur
                        )
                        viewModel.addReminder(reminder)
                        ReminderManager.scheduleReminder(
                            context = context,
                            seriesId = seriesId,
                            drugName = drugName,
                            interval = selectedInterval,
                            daySelection = ds,
                            hour = timePickerState.hour,
                            minute = timePickerState.minute,
                            duration = dur
                        )
                        Toast.makeText(context, "Promemoria impostato!", Toast.LENGTH_SHORT).show()
                        onDismissRequest()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Salva Promemoria", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
