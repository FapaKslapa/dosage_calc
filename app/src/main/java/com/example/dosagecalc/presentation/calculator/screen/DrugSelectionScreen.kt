package com.example.dosagecalc.presentation.calculator.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel

/**
 * Schermata 1: Selezione del farmaco.
 *
 * Mostra un dropdown con tutti i farmaci disponibili nel catalogo locale.
 * Dopo la selezione, abilita il bottone "Avanti" per procedere all'input paziente.
 *
 * Lo stato UI arriva sempre dal ViewModel tramite StateFlow: questa composable
 * è puramente "stupida" — riceve dati, emette eventi.
 */
@Composable
fun DrugSelectionScreen(
    viewModel: CalculatorViewModel,
    onNavigateToInput: () -> Unit
) {
    // collectAsStateWithLifecycle è la best practice Google per raccogliere Flow
    // in Compose: si ferma automaticamente quando il composable va in background
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calcolo Dosaggio") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->

        when {
            // --- Stato: caricamento farmaci in corso ---
            uiState.isLoadingDrugs -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // --- Stato: errore di caricamento ---
            uiState.loadError != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = uiState.loadError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // --- Stato: farmaci caricati, mostra selezione ---
            else -> {
                DrugSelectionContent(
                    drugs           = uiState.availableDrugs,
                    selectedDrug    = uiState.selectedDrug,
                    onDrugSelected  = viewModel::onDrugSelected,
                    onProceed       = onNavigateToInput,
                    modifier        = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * Contenuto della schermata di selezione.
 * Estratto in composable separata per facilitare i Preview e i test UI.
 */
@Composable
private fun DrugSelectionContent(
    drugs: List<Drug>,
    selectedDrug: Drug?,
    onDrugSelected: (Drug) -> Unit,
    onProceed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text  = "Seleziona il Farmaco",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text  = "Scegli il farmaco e l'indicazione clinica dal catalogo.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dropdown farmaci
        DrugDropdown(
            drugs          = drugs,
            selectedDrug   = selectedDrug,
            onDrugSelected = onDrugSelected
        )

        // Se un farmaco è selezionato, mostra i dettagli come anteprima
        if (selectedDrug != null) {
            Spacer(modifier = Modifier.height(16.dp))
            DrugPreviewCard(drug = selectedDrug)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick  = onProceed,
            enabled  = selectedDrug != null,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Avanti: Dati Paziente")
        }
    }
}

/**
 * Dropdown per la selezione del farmaco.
 * Usa ExposedDropdownMenuBox di Material3 per la corretta accessibilità.
 */
@Composable
private fun DrugDropdown(
    drugs: List<Drug>,
    selectedDrug: Drug?,
    onDrugSelected: (Drug) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded    = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier    = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value    = selectedDrug?.let { "${it.name} — ${it.indication}" } ?: "",
            onValueChange = {},
            readOnly = true,
            label    = { Text("Farmaco / Indicazione") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Apri lista farmaci"
                )
            },
            colors   = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded    = expanded,
            onDismissRequest = { expanded = false }
        ) {
            drugs.forEach { drug ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text  = drug.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text  = drug.indication,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onDrugSelected(drug)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Card di anteprima del farmaco selezionato.
 * Mostra il tipo di formula e l'alert clinico prima di procedere.
 */
@Composable
private fun DrugPreviewCard(drug: Drug) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = "Formula: ${drug.formulaType.name.lowercase().replace("_", " ")}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "Dose: ${drug.unitDose} ${drug.unit}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            if (drug.alert.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text  = "⚠ ${drug.alert}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
