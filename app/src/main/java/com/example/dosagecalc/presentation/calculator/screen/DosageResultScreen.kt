package com.example.dosagecalc.presentation.calculator.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel

/**
 * Schermata 3: Risultato del calcolo della dose.
 *
 * Gestisce 3 casi di risultato (sealed class):
 * - [DosageResult.Success]: mostra la dose con formula e tracciabilità
 * - [DosageResult.ValidationError]: mostra gli errori clinici in modo chiaro
 * - [DosageResult.Error]: mostra un messaggio di errore tecnico
 *
 * Il disclaimer medico è SEMPRE visibile: questa è un'app didattica,
 * non un dispositivo medico certificato.
 */
@Composable
fun DosageResultScreen(
    viewModel: CalculatorViewModel,
    onNewCalculation: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result  = uiState.dosageResult

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Risultato Calcolo") },
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

            // Farmaco e indicazione come contesto
            uiState.selectedDrug?.let { drug ->
                Text(
                    text  = "${drug.name} — ${drug.indication}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Risultato principale ---
            when (result) {
                is DosageResult.Success        -> SuccessCard(result)
                is DosageResult.ValidationError -> ValidationErrorCard(result)
                is DosageResult.Error          -> GenericErrorCard(result)
                null                           -> { /* non dovrebbe accadere */ }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Disclaimer medico (sempre visibile) ---
            DisclaimerCard()

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Bottone nuovo calcolo
            Button(
                onClick  = onNewCalculation,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Nuovo Calcolo")
            }

            // Se c'è stato un errore di validazione, permettiamo di correggere i dati
            if (result is DosageResult.ValidationError) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick  = { viewModel.resetCalculation() /* torna indietro */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Correggi i Dati")
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Card risultato successo
// -----------------------------------------------------------------------------

@Composable
private fun SuccessCard(result: DosageResult.Success) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text  = "Dose Calcolata",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dose totale in grande: il dato più importante
            Text(
                text       = "${formatDose(result.totalDose)} ${result.unit}",
                fontSize   = 40.sp,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary
            )

            // Avviso cappatura dose massima
            if (result.cappedToMaxDose) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = "⚠ Dose ridotta al massimo consentito per sicurezza",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Formula di calcolo (tracciabilità)
            Text(
                text  = "Formula applicata",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = result.formula,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Alert clinico
            if (result.alert.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text  = "Avviso clinico",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = result.alert,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Fonte bibliografica
            if (result.source.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text      = "Fonte: ${result.source}",
                    style     = MaterialTheme.typography.labelSmall,
                    color     = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Card errore di validazione clinica
// -----------------------------------------------------------------------------

@Composable
private fun ValidationErrorCard(result: DosageResult.ValidationError) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Filled.Warning,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.error,
                    modifier           = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text  = "Farmaco Non Indicato",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text  = result.reason,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Card errore tecnico generico
// -----------------------------------------------------------------------------

@Composable
private fun GenericErrorCard(result: DosageResult.Error) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text  = "Errore di Calcolo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text  = result.message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Disclaimer medico
// -----------------------------------------------------------------------------

@Composable
private fun DisclaimerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = "DISCLAIMER",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "Questo strumento ha finalità esclusivamente didattiche. " +
                        "Il calcolo non sostituisce la valutazione clinica del medico. " +
                        "Verificare sempre il dosaggio sulla scheda tecnica ufficiale (RCP/AIFA).",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Utility
// -----------------------------------------------------------------------------

/**
 * Formatta la dose per la visualizzazione:
 * - Se è un numero intero, non mostra decimali (es. 3600 invece di 3600.0)
 * - Altrimenti mostra fino a 2 decimali significativi
 */
private fun formatDose(dose: Double): String {
    return if (dose == dose.toLong().toDouble()) {
        dose.toLong().toString()
    } else {
        String.format("%.2f", dose)
    }
}
