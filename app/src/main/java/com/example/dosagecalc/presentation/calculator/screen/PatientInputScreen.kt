package com.example.dosagecalc.presentation.calculator.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.domain.model.FormulaType
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel

/**
 * Schermata 2: Input dati del paziente.
 *
 * Mostra i campi di input rilevanti per il farmaco selezionato:
 * - Peso sempre visibile (obbligatorio per PER_KG e PER_M2)
 * - Altezza visibile solo se la formula richiede il BSA (PER_M2)
 * - Età sempre visibile (per i controlli di sicurezza sull'età minima)
 *
 * La validazione avviene in tempo reale nel ViewModel: la UI mostra
 * gli errori sotto ogni campo senza logica aggiuntiva.
 */
@Composable
fun PatientInputScreen(
    viewModel: CalculatorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigazione automatica al risultato appena il calcolo è pronto.
    // LaunchedEffect si riesegue solo quando dosageResult cambia.
    LaunchedEffect(uiState.dosageResult) {
        if (uiState.dosageResult != null) {
            onNavigateToResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.selectedDrug?.name ?: "Dati Paziente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Torna indietro"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text  = "Dati Antropometrici",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text  = "Compila i campi per il paziente. I valori vengono validati in tempo reale.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Campo Peso (sempre visibile) ---
            OutlinedTextField(
                value         = uiState.weightInput,
                onValueChange = viewModel::onWeightChanged,
                label         = { Text("Peso (kg) *") },
                placeholder   = { Text("es. 70.5") },
                isError       = uiState.weightError != null,
                supportingText = {
                    uiState.weightError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        ?: uiState.selectedDrug?.minWeightKg?.let {
                            Text("Minimo: $it kg", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction    = ImeAction.Next
                ),
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Campo Altezza (solo se la formula usa il BSA) ---
            if (uiState.selectedDrug?.formulaType == FormulaType.PER_M2) {
                OutlinedTextField(
                    value         = uiState.heightInput,
                    onValueChange = viewModel::onHeightChanged,
                    label         = { Text("Altezza (cm) *") },
                    placeholder   = { Text("es. 175") },
                    isError       = uiState.heightError != null,
                    supportingText = {
                        uiState.heightError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                            ?: Text("Necessaria per il calcolo BSA (Mosteller)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction    = ImeAction.Next
                    ),
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- Campo Età ---
            OutlinedTextField(
                value         = uiState.ageInput,
                onValueChange = viewModel::onAgeChanged,
                label         = { Text("Età (anni)") },
                placeholder   = { Text("es. 35") },
                isError       = uiState.ageError != null,
                supportingText = {
                    uiState.ageError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        ?: uiState.selectedDrug?.minAgeYears?.let {
                            Text("Età minima: $it anni", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction    = ImeAction.Done
                ),
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Bottone calcola: disabilitato se i dati non sono validi
            Button(
                onClick  = viewModel::calculateDosage,
                enabled  = uiState.canCalculate && !uiState.isCalculating,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (uiState.isCalculating) {
                    Text("Calcolo in corso...")
                } else {
                    Text("Calcola Dose")
                }
            }
        }
    }
}
