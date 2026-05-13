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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.Reminder
import com.example.dosagecalc.domain.model.ReminderInterval
import com.example.dosagecalc.presentation.calculator.RemindersViewModel
import com.example.dosagecalc.presentation.ui.components.PillButton
import com.example.dosagecalc.presentation.ui.components.RoundedTextField
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.spacing
import com.example.dosagecalc.presentation.utils.ReminderManager
import java.util.Locale
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
    val shapes = LocalDosageShapes.current
    val sp = MaterialTheme.spacing

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = shapes.sheet
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = sp.xl)
                .padding(bottom = sp.xl)
        ) {
            Text(
                text = "Promemoria Attivi",
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(sp.base))
            if (reminders.isEmpty()) {
                Text(
                    text = "Nessun promemoria attualmente impostato.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                    items(reminders) { reminder ->
                        val timeString = String.format(Locale.US, "%02d:%02d", reminder.hour, reminder.minute)
                        val intervalText = when (reminder.interval) {
                            ReminderInterval.DAILY -> "Giornaliero"
                            ReminderInterval.WEEKLY -> "Settimanale · G${reminder.daySelection}"
                            ReminderInterval.MONTHLY -> "Mensile · G${reminder.daySelection}"
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = sp.xs),
                            shape = shapes.card,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(sp.base),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        shape = shapes.pill,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = timeString,
                                            style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Serif),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = sp.sm, vertical = sp.xs)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(sp.xs))
                                    Text(
                                        text = reminder.drugName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = intervalText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = shapes.pill,
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                ) {
                                    IconButton(onClick = {
                                        ReminderManager.cancelReminderSeries(context, reminder.id)
                                        viewModel.deleteReminder(reminder.id)
                                        Toast.makeText(context, "Promemoria eliminato", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Elimina",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(sp.lg))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(sp.base))
            Text(
                text = "Crea Nuovo Promemoria",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(sp.base))
            if (drugName == null) {
                Text(
                    "Esegui prima un calcolo per un farmaco.",
                    modifier = Modifier.padding(vertical = sp.base),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                Spacer(modifier = Modifier.height(sp.sm))
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ReminderInterval.entries.forEachIndexed { index, interval ->
                        SegmentedButton(
                            selected = selectedInterval == interval,
                            onClick = { selectedInterval = interval },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = ReminderInterval.entries.size),
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

                Spacer(modifier = Modifier.height(sp.base))

                Text(
                    text = "Orario della somministrazione",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(sp.sm))
                val timePickerColors = TimePickerDefaults.colors(
                    clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    selectorColor = MaterialTheme.colorScheme.primary,
                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = shapes.card,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(sp.base),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompact) {
                            TimeInput(state = timePickerState, colors = timePickerColors)
                        } else {
                            TimePicker(state = timePickerState, colors = timePickerColors)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(sp.base))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(sp.base)
                ) {
                    RoundedTextField(
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
                        singleLine = true,
                        shape = shapes.card
                    )

                    if (selectedInterval == ReminderInterval.MONTHLY) {
                        RoundedTextField(
                            value = daySelection,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) daySelection = it },
                            label = { Text("Giorno (1-31)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = shapes.card
                        )
                    }
                }

                if (selectedInterval == ReminderInterval.WEEKLY) {
                    Spacer(modifier = Modifier.height(sp.base))
                    Text(
                        text = "Giorno della settimana",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(sp.sm))
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
                                shape = shapes.chip,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(sp.lg))
                PillButton(
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
                    label = "Salva Promemoria",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
