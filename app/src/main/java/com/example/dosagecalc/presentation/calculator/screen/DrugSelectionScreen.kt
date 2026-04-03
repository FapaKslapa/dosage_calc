package com.example.dosagecalc.presentation.calculator.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel
import com.example.dosagecalc.presentation.calculator.components.DashboardShortcuts
import com.example.dosagecalc.presentation.calculator.components.DrugPreviewCard
import com.example.dosagecalc.presentation.calculator.components.DrugSelectionCard
import com.example.dosagecalc.presentation.ui.components.GradientBottomBar
import com.example.dosagecalc.presentation.ui.components.GradientScreenHeader

@Composable
fun DrugSelectionScreen(
    viewModel: CalculatorViewModel,
    onNavigateToInput: () -> Unit,
    onNavigateToPatients: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToAddData: (String?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var drugToDelete by remember { mutableStateOf<com.example.dosagecalc.domain.model.Drug?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            GradientScreenHeader(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-30).dp, y = 36.dp)
                        .background(Color.White.copy(alpha = 0.06f), CircleShape)
                )

                Column(modifier = Modifier.padding(start = 24.dp, top = 40.dp)) {
                    Text(
                        text  = "Bentornato,\nDottore.",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            fontSize = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text  = "Cosa desideri fare oggi?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            DashboardShortcuts(
                onNavigateToPatients = onNavigateToPatients,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToReminders = onNavigateToReminders,
                onNavigateToAddData = { onNavigateToAddData(null) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 0.dp)
                    .padding(bottom = 120.dp)    
            ) {
                Text(
                    text  = "Schede Farmaci",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Serif),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Cerca farmaco...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cerca") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                when {
                    uiState.isLoadingDrugs -> {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }
                    uiState.loadError != null -> {
                        Text(
                            text  = uiState.loadError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    else -> {
                        val filteredDrugs = uiState.availableDrugs.filter {
                            it.name.contains(uiState.searchQuery, ignoreCase = true) ||
                            it.indication.contains(uiState.searchQuery, ignoreCase = true)
                        }

                        LazyRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item { Spacer(modifier = Modifier.width(20.dp)) }
                            items(filteredDrugs) { drug ->
                                DrugSelectionCard(
                                    drug = drug,
                                    isSelected = uiState.selectedDrug == drug,
                                    onClick = { viewModel.onDrugSelected(drug) },
                                    onDeleteClick = if (drug.id.startsWith("custom_")) {
                                        { drugToDelete = drug }
                                    } else null,
                                    onEditClick = if (drug.id.startsWith("custom_")) {
                                        { onNavigateToAddData(drug.id) }
                                    } else null
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            item { Spacer(modifier = Modifier.width(4.dp)) }
                        }
                    }
                }

                if (uiState.selectedDrug != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        DrugPreviewCard(drug = uiState.selectedDrug!!)
                    }
                }
            }
        }

        if (drugToDelete != null) {
            AlertDialog(
                onDismissRequest = { drugToDelete = null },
                title = { Text("Eliminare ${drugToDelete?.name}?") },
                text = { Text("Sei sicuro di voler eliminare definitivamente questo farmaco personalizzato?") },
                confirmButton = {
                    TextButton(onClick = {
                        val id = drugToDelete?.id
                        if (id != null) {
                            viewModel.deleteCustomDrug(id)
                        }
                        drugToDelete = null
                    }) {
                        Text("Elimina", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { drugToDelete = null }) {
                        Text("Annulla")
                    }
                }
            )
        }

        GradientBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                onClick  = onNavigateToInput,
                enabled  = uiState.selectedDrug != null && !uiState.isLoadingDrugs,
                shape    = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text  = "Avanti: Dati Paziente",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
