package com.example.dosagecalc.presentation.calculator.screen

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel
import com.example.dosagecalc.presentation.calculator.components.AlertCard
import com.example.dosagecalc.presentation.calculator.components.DetailsCard
import com.example.dosagecalc.presentation.calculator.components.DisclaimerCard
import com.example.dosagecalc.presentation.calculator.components.ErrorHeader
import com.example.dosagecalc.presentation.calculator.components.RemindersSheet
import com.example.dosagecalc.presentation.calculator.components.SuccessHeader
import com.example.dosagecalc.presentation.ui.components.GradientBottomBar
import com.example.dosagecalc.presentation.utils.PdfManager
import com.example.dosagecalc.presentation.utils.ReminderManager

@Composable
fun DosageResultScreen(
    viewModel: CalculatorViewModel,
    onNewCalculation: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result  = uiState.dosageResult ?: return   

    val context = LocalContext.current
    val canExportPdf = result is DosageResult.Success && uiState.selectedDrug != null

    var showReminderSheet by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showReminderSheet = true
        } else {
            Toast.makeText(context, "Permesso notifiche negato", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                        
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            if (uiState.selectedPatient != null) {
                                OutlinedButton(
                                    onClick = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            showReminderSheet = true
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Promemoria")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Promemoria")
                                }
                            }
                            
                            if (canExportPdf) {
                                if (uiState.selectedPatient != null) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                OutlinedButton(
                                    onClick = {
                                         PdfManager.generateAndSharePdf(context, uiState.selectedDrug!!, uiState.selectedPatient, result)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.Share, contentDescription = "PDF")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Esporta PDF")
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

            if (showReminderSheet) {
                RemindersSheet(
                    context = context,
                    drugName = uiState.selectedDrug?.name,
                    onDismissRequest = { showReminderSheet = false }
                )
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
}
