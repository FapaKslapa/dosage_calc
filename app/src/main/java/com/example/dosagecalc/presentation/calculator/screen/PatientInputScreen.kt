package com.example.dosagecalc.presentation.calculator.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.FormulaType
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel
import com.example.dosagecalc.presentation.calculator.components.PatientInputField
import com.example.dosagecalc.presentation.ui.components.GradientBottomBar
import com.example.dosagecalc.presentation.ui.components.GradientScreenHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientInputScreen(
    viewModel: CalculatorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.dosageResult) {
        if (uiState.dosageResult != null) onNavigateToResult()
    }

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
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro",
                                tint               = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text  = "Dati del Paziente",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }

                    uiState.selectedDrug?.let { drug ->
                        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)) {
                            Text(
                                text  = drug.name,
                                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Serif),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text  = drug.indication,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 100.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.savedPatients.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedPatient?.let { "${it.name} ${it.surname}" } ?: "Calcolo Anonimo",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleziona Paziente (Opzionale)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = OutlinedTextFieldDefaults.colors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Calcolo Anonimo (Nuovo)", style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    viewModel.onPatientSelected(null)
                                    expanded = false
                                }
                            )
                            uiState.savedPatients.forEach { patient ->
                                DropdownMenuItem(
                                    text = { Text("${patient.name} ${patient.surname}", style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        viewModel.onPatientSelected(patient)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text  = "Dati Antropometrici",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text  = "Valori validati in tempo reale",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        val weightVal = uiState.weightInput.toFloatOrNull() ?: 0f
                        PatientInputField(
                            label = "Peso",
                            value = uiState.weightInput.ifEmpty { "" },
                            onValueChange = viewModel::onWeightChanged,
                            sliderValue = weightVal,
                            onSliderChange = { viewModel.onWeightChanged(String.format(java.util.Locale.US, "%.1f", it)) },
                            sliderRange = 1f..150f,
                            suffix = "kg",
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.primaryContainer,
                            errorMessage = uiState.weightError,
                            hintMessage = uiState.selectedDrug?.minWeightKg?.let { "Minimo richiesto: $it kg" }
                        )

                        if (uiState.selectedDrug?.formulaType == FormulaType.PER_M2) {
                            Spacer(modifier = Modifier.height(24.dp))
                            val heightVal = uiState.heightInput.toFloatOrNull() ?: 0f
                            PatientInputField(
                                label = "Altezza",
                                value = uiState.heightInput.ifEmpty { "" },
                                onValueChange = viewModel::onHeightChanged,
                                sliderValue = heightVal,
                                onSliderChange = { viewModel.onHeightChanged(it.toInt().toString()) },
                                sliderRange = 10f..250f,
                                suffix = "cm",
                                activeColor = MaterialTheme.colorScheme.secondary,
                                inactiveColor = MaterialTheme.colorScheme.secondaryContainer,
                                errorMessage = uiState.heightError,
                                hintMessage = "Necessaria per il calcolo BSA (Mosteller)"
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        val ageVal = uiState.ageInput.toFloatOrNull() ?: 0f
                        PatientInputField(
                            label = "Età",
                            value = uiState.ageInput.ifEmpty { "" },
                            onValueChange = viewModel::onAgeChanged,
                            sliderValue = ageVal,
                            onSliderChange = { viewModel.onAgeChanged(it.toInt().toString()) },
                            sliderRange = 0f..120f,
                            suffix = "anni",
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                            activeColor = MaterialTheme.colorScheme.tertiary,
                            inactiveColor = MaterialTheme.colorScheme.tertiaryContainer,
                            errorMessage = uiState.ageError,
                            hintMessage = uiState.selectedDrug?.minAgeYears?.let { "Età minima richiesta: $it anni" }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text  = "Patologie Concomitanti",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            FilterChip(
                                selected = uiState.hasRenalImpairment,
                                onClick = { viewModel.onRenalImpairmentChanged(!uiState.hasRenalImpairment) },
                                label = { Text("Insufficienza Renale") },
                                leadingIcon = if (uiState.hasRenalImpairment) {
                                    { Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            )

                            FilterChip(
                                selected = uiState.hasHepaticImpairment,
                                onClick = { viewModel.onHepaticImpairmentChanged(!uiState.hasHepaticImpairment) },
                                label = { Text("Insufficienza Epatica") },
                                leadingIcon = if (uiState.hasHepaticImpairment) {
                                    { Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            )
                        }
                        
                    }
                }
            }
        }

        GradientBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                onClick  = viewModel::calculateDosage,
                enabled  = uiState.canCalculate && !uiState.isCalculating,
                shape    = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (uiState.isCalculating) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(24.dp),
                        color     = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text  = "Calcola Dose",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
