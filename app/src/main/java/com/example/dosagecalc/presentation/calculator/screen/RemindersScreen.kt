package com.example.dosagecalc.presentation.calculator.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GradientScreenHeader(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primaryContainer
                ),
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
                                tint               = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text  = "Calendario",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                    Column(modifier = Modifier.padding(start = sp.xl, end = sp.xl, top = sp.xs)) {
                        Text(
                            text  = "Promemoria",
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(sp.sm))
                        Text(
                            text  = "Gestisci i tuoi allarmi attivi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(sp.xl))
                    }
                }
            }

            if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyStateView(
                        icon = Icons.Default.AlarmOff,
                        title = "Nessun promemoria",
                        subtitle = "Crea un promemoria dalla schermata risultato dopo aver calcolato una dose"
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = sp.base, vertical = sp.xl),
                    horizontalArrangement = Arrangement.spacedBy(sp.md),
                    verticalArrangement = Arrangement.spacedBy(sp.md)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            Text(
                                text = "Promemoria Attivi",
                                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(sp.xs))
                        }
                    }
                    items(reminders, key = { it.id }) { reminder ->
                        var showDeleteDialog by remember { mutableStateOf(false) }
                        val timeString = String.format("%02d:%02d", reminder.hour, reminder.minute)
                        val frequencyText = when (reminder.interval) {
                            ReminderInterval.DAILY   -> "Giornaliero"
                            ReminderInterval.WEEKLY  -> "Sett. G.${reminder.daySelection}"
                            ReminderInterval.MONTHLY -> "Mens. G.${reminder.daySelection}"
                        }

                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = { Text("Elimina Promemoria") },
                                text = { Text("Vuoi eliminare il promemoria per ${reminder.drugName} alle $timeString?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        ReminderManager.cancelReminderSeries(context, reminder.id)
                                        viewModel.deleteReminder(reminder.id)
                                        Toast.makeText(context, "Promemoria cancellato", Toast.LENGTH_SHORT).show()
                                        showDeleteDialog = false
                                    }) {
                                        Text("Elimina", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteDialog = false }) {
                                        Text("Annulla")
                                    }
                                }
                            )
                        }

                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .animateItem(),
                            shape = shapes.expressive,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(sp.md)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Alarm,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .align(Alignment.TopEnd),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.07f)
                                )
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = timeString,
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 38.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = reminder.drugName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Surface(
                                            shape = shapes.chip,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = frequencyText,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = sp.sm, vertical = sp.xs),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        IconButton(
                                            onClick = { showDeleteDialog = true }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Elimina Promemoria",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(20.dp)
                                            )
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
