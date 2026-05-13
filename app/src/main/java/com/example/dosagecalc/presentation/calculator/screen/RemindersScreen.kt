package com.example.dosagecalc.presentation.calculator.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.ReminderInterval
import com.example.dosagecalc.presentation.calculator.RemindersViewModel
import com.example.dosagecalc.presentation.ui.components.EmptyStateView
import com.example.dosagecalc.presentation.ui.components.GradientScreenHeader
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.spacing
import com.example.dosagecalc.presentation.utils.ReminderManager

@Composable
fun RemindersScreen(
    viewModel: RemindersViewModel,
    onNavigateBack: () -> Unit
) {
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sp = MaterialTheme.spacing
    val shapes = LocalDosageShapes.current
    val cs = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GradientScreenHeader(
                colors = listOf(cs.primary, cs.primaryContainer),
                modifier = Modifier.padding(bottom = 0.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = sp.sm)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro",
                                tint               = cs.onPrimary
                            )
                        }
                        Text(
                            text  = "Calendario",
                            style = MaterialTheme.typography.titleMedium,
                            color = cs.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                    Column(modifier = Modifier.padding(start = sp.xl, end = sp.xl, top = sp.xs)) {
                        Text(
                            text  = "Promemoria",
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                            color = cs.onPrimary
                        )
                        Spacer(modifier = Modifier.height(sp.sm))
                        Text(
                            text  = "Gestisci i tuoi allarmi attivi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = cs.onPrimary.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(sp.xl))
                    }
                }
            }

            if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyStateView(
                        icon     = Icons.Default.AlarmOff,
                        title    = "Nessun promemoria",
                        subtitle = "Crea un promemoria dalla schermata risultato dopo aver calcolato una dose"
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(sp.xl))

                    Row(
                        modifier = Modifier.padding(horizontal = sp.xl),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Attivi",
                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                            color = cs.onBackground
                        )
                        Spacer(modifier = Modifier.width(sp.sm))
                        Surface(
                            shape = shapes.chip,
                            color = cs.primaryContainer
                        ) {
                            Text(
                                text = "${reminders.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = cs.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = sp.sm, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(sp.base))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = sp.xl),
                        horizontalArrangement = Arrangement.spacedBy(sp.md)
                    ) {
                        itemsIndexed(reminders, key = { _, item -> item.id }) { _, reminder ->
                            var showDeleteDialog by remember { mutableStateOf(false) }
                            val timeString = String.format("%02d:%02d", reminder.hour, reminder.minute)
                            val frequencyText = when (reminder.interval) {
                                ReminderInterval.DAILY   -> "Giornaliero"
                                ReminderInterval.WEEKLY  -> "Settimanale · G${reminder.daySelection}"
                                ReminderInterval.MONTHLY -> "Mensile · G${reminder.daySelection}"
                            }
                            val durationText = when (reminder.interval) {
                                ReminderInterval.DAILY   -> "${reminder.durationDays} giorni"
                                ReminderInterval.WEEKLY  -> "${reminder.durationDays} settimane"
                                ReminderInterval.MONTHLY -> "${reminder.durationDays} mesi"
                            }

                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteDialog = false },
                                    title = { Text("Elimina Promemoria") },
                                    text = { Text("Vuoi eliminare il promemoria per ${reminder.drugName} alle $timeString?") },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                ReminderManager.cancelReminderSeries(context, reminder.id)
                                                viewModel.deleteReminder(reminder.id)
                                                Toast.makeText(context, "Promemoria cancellato", Toast.LENGTH_SHORT).show()
                                                showDeleteDialog = false
                                            },
                                            colors = ButtonDefaults.textButtonColors(contentColor = cs.error)
                                        ) { Text("Elimina") }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDeleteDialog = false }) { Text("Annulla") }
                                    }
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .width(200.dp)
                                    .wrapContentHeight(),
                                shape = shapes.card,
                                colors = CardDefaults.cardColors(
                                    containerColor = cs.secondaryContainer.copy(alpha = 0.5f)
                                ),
                                border = BorderStroke(0.5.dp, cs.secondary.copy(alpha = 0.25f))
                            ) {
                                Column(modifier = Modifier.padding(sp.base)) {
                                    Surface(
                                        shape = shapes.chip,
                                        color = cs.secondary.copy(alpha = 0.13f)
                                    ) {
                                        Text(
                                            text = timeString,
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = cs.secondary,
                                            modifier = Modifier.padding(horizontal = sp.base, vertical = sp.xs)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(sp.sm))

                                    Text(
                                        text = reminder.drugName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = cs.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(sp.xs))

                                    Surface(shape = shapes.chip, color = cs.surface) {
                                        Text(
                                            text = frequencyText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = cs.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = sp.sm, vertical = 3.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(sp.xs))

                                    Text(
                                        text = durationText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = cs.onSurfaceVariant.copy(alpha = 0.75f)
                                    )

                                    Spacer(modifier = Modifier.height(sp.sm))
                                    HorizontalDivider(
                                        color = cs.secondary.copy(alpha = 0.15f),
                                        thickness = 0.5.dp
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = { showDeleteDialog = true },
                                            shape = shapes.chip,
                                            colors = ButtonDefaults.textButtonColors(contentColor = cs.error),
                                            contentPadding = PaddingValues(horizontal = sp.sm, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Elimina",
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Elimina", style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
