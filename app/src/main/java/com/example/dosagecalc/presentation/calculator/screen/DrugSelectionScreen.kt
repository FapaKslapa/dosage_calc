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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    onNavigateToHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                onNavigateToHistory = onNavigateToHistory
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .padding(bottom = 120.dp)    
            ) {
                Text(
                    text  = "Schede Farmaci",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Serif),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp)
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
                        LazyRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item { Spacer(modifier = Modifier.width(20.dp)) }
                            items(uiState.availableDrugs) { drug ->
                                DrugSelectionCard(
                                    drug = drug,
                                    isSelected = uiState.selectedDrug == drug,
                                    onClick = { viewModel.onDrugSelected(drug) }
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
