package com.example.dosagecalc.presentation.calculator.screen

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
