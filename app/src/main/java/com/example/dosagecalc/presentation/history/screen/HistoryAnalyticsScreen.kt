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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dosagecalc.domain.model.Patient
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel
import com.example.dosagecalc.presentation.history.HistoryViewModel
import com.example.dosagecalc.presentation.history.components.DoseTrendChart
import com.example.dosagecalc.presentation.ui.components.GradientScreenHeader

@Composable
fun HistoryAnalyticsScreen(
    historyViewModel: HistoryViewModel = hiltViewModel(),
    calculatorViewModel: CalculatorViewModel,
    initialPatientId: String? = null,
    onNavigateBack: () -> Unit
) {
    val history by historyViewModel.getAllHistoryFlow().collectAsState(initial = emptyList())
    val drugsState by calculatorViewModel.uiState.collectAsState()
    
    var selectedDrugName by remember { mutableStateOf<String?>(null) }
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }

    // Pre-selezione del paziente se arriviamo da un paziente specifico
    LaunchedEffect(initialPatientId, drugsState.savedPatients) {
        if (initialPatientId != null && selectedPatient == null) {
            selectedPatient = drugsState.savedPatients.find { it.id == initialPatientId }
        }
    }

    val filteredHistory = remember(history, selectedDrugName, selectedPatient) {
        history.filter { record ->
            val matchDrug = selectedDrugName == null || record.drugName == selectedDrugName
            val matchPatient = selectedPatient == null || record.patientId == selectedPatient?.id
            matchDrug && matchPatient
        }
    }

    val categoryDist = remember(history, drugsState.availableDrugs) {
        historyViewModel.getCategoryDistribution(history, drugsState.availableDrugs)
    }

    val uniqueDrugs = remember(history) { historyViewModel.getUniqueDrugsInHistory(history) }
    val uniquePatients = remember(history, drugsState.savedPatients) { 
        historyViewModel.getUniquePatientsInHistory(history, drugsState.savedPatients) 
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
                )
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro",
                                tint = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                        Text(
                            text = "Analisi Dati",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                        )
                    }
                    
                    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)) {
                        Text(
                            text = "Statistiche",
                            style = MaterialTheme.typography.displaySmall.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                        Text(
                            text = if (selectedDrugName == null) "Panoramica globale" else "Trend: $selectedDrugName",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                StatsSummaryCard(filteredHistory.size, categoryDist.size)

                // Filtri Dinamici
                FilterSection(
                    drugs = uniqueDrugs,
                    patients = uniquePatients,
                    selectedDrug = selectedDrugName,
                    selectedPatient = selectedPatient,
                    onDrugSelected = { selectedDrugName = it },
                    onPatientSelected = { selectedPatient = it }
                )

                if (selectedDrugName != null && filteredHistory.size >= 2) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        DoseTrendChart(records = filteredHistory)
                    }
                } else if (selectedDrugName == null) {
                    InfoNote(text = "Seleziona un farmaco specifico dai filtri per visualizzare l'andamento del dosaggio nel tempo.")
                } else {
                    InfoNote(text = "Dati insufficienti per generare un grafico di trend per questo farmaco.")
                }

                if (selectedDrugName == null && categoryDist.isNotEmpty()) {
                    CategoryDistributionCard(categoryDist)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FilterSection(
    drugs: List<String>,
    patients: List<Patient>,
    selectedDrug: String?,
    selectedPatient: Patient?,
    onDrugSelected: (String?) -> Unit,
    onPatientSelected: (Patient?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Filtra Risultati", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.tertiary)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            var drugExpanded by remember { mutableStateOf(false) }
            var patientExpanded by remember { mutableStateOf(false) }

            // Filtro Farmaco
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { drugExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = selectedDrug ?: "Tutti i Farmaci",
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                DropdownMenu(expanded = drugExpanded, onDismissRequest = { drugExpanded = false }) {
                    DropdownMenuItem(text = { Text("Tutti i Farmaci") }, onClick = { onDrugSelected(null); drugExpanded = false })
                    drugs.forEach { name ->
                        DropdownMenuItem(text = { Text(name) }, onClick = { onDrugSelected(name); drugExpanded = false })
                    }
                }
            }

            // Filtro Paziente
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { patientExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = selectedPatient?.let { "${it.name} ${it.surname}" } ?: "Tutti i Pazienti",
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                DropdownMenu(expanded = patientExpanded, onDismissRequest = { patientExpanded = false }) {
                    DropdownMenuItem(text = { Text("Tutti i Pazienti") }, onClick = { onPatientSelected(null); patientExpanded = false })
                    patients.forEach { p ->
                        DropdownMenuItem(text = { Text("${p.name} ${p.surname}") }, onClick = { onPatientSelected(p); patientExpanded = false })
                    }
                }
            }
        }
    }
}

@Composable
fun StatsSummaryCard(totalCalculations: Int, totalCategories: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = totalCalculations.toString(), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                Text(text = "Calcoli", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            VerticalDivider(modifier = Modifier.height(40.dp).align(Alignment.CenterVertically), color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = totalCategories.toString(), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                Text(text = "Categorie", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CategoryDistributionCard(dist: Map<String, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Distribuzione per Categoria", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            val total = dist.values.sum().toFloat()
            
            dist.forEach { (cat, count) ->
                val progress = count / total
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = cat, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoNote(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
