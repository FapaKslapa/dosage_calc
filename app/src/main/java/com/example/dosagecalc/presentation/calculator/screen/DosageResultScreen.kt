package com.example.dosagecalc.presentation.calculator.screen

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel
import com.example.dosagecalc.presentation.calculator.components.AlertCard
import com.example.dosagecalc.presentation.calculator.components.DetailsCard
import com.example.dosagecalc.presentation.calculator.components.DisclaimerCard
import com.example.dosagecalc.presentation.calculator.components.ErrorHeader
import com.example.dosagecalc.presentation.calculator.components.SuccessHeader
import com.example.dosagecalc.presentation.ui.components.GradientBottomBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import android.widget.Toast

@Composable
fun DosageResultScreen(
    viewModel: CalculatorViewModel,
    onNewCalculation: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result  = uiState.dosageResult ?: return   

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

            when (result) {
                is DosageResult.Success         -> SuccessHeader(result, uiState.selectedDrug?.let { "${it.name} — ${it.indication}" })
                is DosageResult.ValidationError -> ErrorHeader(title = "Farmaco Non Indicato", message = result.reason)
                is DosageResult.Error           -> ErrorHeader(title = "Errore di Calcolo",    message = result.message)
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 108.dp)    
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                if (result is DosageResult.Success) {
                    
                    val context = LocalContext.current
                    if (uiState.selectedPatient != null) {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Il calendario in-app sarà disponibile a breve!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.DateRange, contentDescription = "Calendario")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Calendario")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Prescrizione:\nFarmaco: ${uiState.selectedDrug?.name}\nPaziente: ${uiState.selectedPatient?.name} ${uiState.selectedPatient?.surname}\nDose: ${result.totalDose} ${result.unit}\nIndicazione: ${uiState.selectedDrug?.indication}")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Condividi prescrizione")
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Condividi")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Condividi")
                            }
                        }
                    }

                    DetailsCard(result)

                    if (result.alert.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AlertCard(result.alert)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                DisclaimerCard()
            }
        }

        GradientBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                onClick  = onNewCalculation,
                shape    = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(
                    text  = "Nuovo Calcolo",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (result is DosageResult.ValidationError) {
                Spacer(modifier = Modifier.height(8.dp))
                FilledTonalButton(
                    onClick  = viewModel::resetCalculation,
                    shape    = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Correggi i Dati")
                }
            }
        }
    }
}
