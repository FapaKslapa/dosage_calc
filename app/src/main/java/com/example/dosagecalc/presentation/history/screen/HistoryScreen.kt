package com.example.dosagecalc.presentation.history.screen

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.dosagecalc.presentation.history.HistoryViewModel
import com.example.dosagecalc.presentation.history.components.HistoryCard
import com.example.dosagecalc.presentation.ui.util.isCompactHeight
import com.example.dosagecalc.presentation.ui.components.EmptyStateView
import com.example.dosagecalc.presentation.ui.components.GradientScreenHeader
import com.example.dosagecalc.presentation.utils.ExportManager

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    patientId: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    val context = LocalContext.current
    val exportManager = remember { ExportManager(context) }
    val isCompact = isCompactHeight()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredPatientName by viewModel.filteredPatientName.collectAsStateWithLifecycle()
    val pagedHistory = viewModel.historyPaged.collectAsLazyPagingItems()

    LaunchedEffect(patientId) {
        if (patientId != null) {
            viewModel.setFilterPatientId(patientId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GradientScreenHeader(
                colors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.padding(bottom = 0.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro",
                                tint               = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                        Text(
                            text  = "Cronologia",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        
                        IconButton(onClick = onNavigateToAnalytics) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "Statistiche",
                                tint = MaterialTheme.colorScheme.onTertiary
                            )
                        }

                        IconButton(onClick = {
                            viewModel.getAllHistory { list ->
                                exportManager.exportHistoryToCsv(list)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Esporta CSV",
                                tint = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)) {
                        Text(
                            text  = filteredPatientName ?: "Storico Calcoli",
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text  = if (filteredPatientName != null) "Cronologia specifica" else "Tutte le dosi calcolate",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.9f),
                                modifier = Modifier.weight(1f)
                            )
                            if (filteredPatientName != null) {
                                TextButton(
                                    onClick = { viewModel.setFilterPatientId(null) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Rimuovi filtro", color = MaterialTheme.colorScheme.onTertiary, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 16.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            placeholder = { Text("Cerca per farmaco, paziente...") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Cerca") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            singleLine = true
                        )
                    }
                }
            }

            if (pagedHistory.itemCount == 0 && uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (pagedHistory.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (searchQuery.isBlank()) {
                        EmptyStateView(
                            icon = Icons.Default.History,
                            title = "Nessun calcolo",
                            subtitle = "I calcoli effettuati appariranno qui con tutti i dati del paziente"
                        )
                    } else {
                        EmptyStateView(
                            icon = Icons.Default.SearchOff,
                            title = "Nessun risultato",
                            subtitle = "Nessun calcolo corrisponde a \"$searchQuery\""
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 320.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(count = pagedHistory.itemCount) { index ->
                        val record = pagedHistory[index]
                        if (record != null) {
                            var showDeleteDialog by remember { mutableStateOf(false) }

                            HistoryCard(
                                record = record,
                                onDeleteClick = { showDeleteDialog = true }
                            )

                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteDialog = false },
                                    title = { Text("Elimina Record") },
                                    text = { Text("Sei sicuro di voler eliminare traccia di questo calcolo?") },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            viewModel.deleteRecord(record.id)
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
                        }
                    }
                }
            }
        }
    }
}
