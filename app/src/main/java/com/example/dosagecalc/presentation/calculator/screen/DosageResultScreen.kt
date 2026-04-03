package com.example.dosagecalc.presentation.calculator.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel
import java.util.Locale

/**
 * Screen 3: Dose calculation result.
 *
 * Design: hero header showing the large dose (success) or
 * the error state with tinted background. Floating cards for details.
 * The disclaimer is always visible at the bottom.
 */
@Composable
fun DosageResultScreen(
    viewModel: CalculatorViewModel,
    onNewCalculation: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result  = uiState.dosageResult ?: return   // guard: should never be null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Scrollable Content ────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Hero Header ──────────────────────────────────────────────
            when (result) {
                is DosageResult.Success         -> SuccessHeader(result, uiState.selectedDrug?.let { "${it.name} — ${it.indication}" })
                is DosageResult.ValidationError -> ErrorHeader(title = "Farmaco Non Indicato", message = result.reason)
                is DosageResult.Error           -> ErrorHeader(title = "Errore di Calcolo",    message = result.message)
            }

            // ── Detail Cards ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 108.dp)    // space for floating buttons
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                if (result is DosageResult.Success) {
                    // Formula and bibliographic source card
                    DetailsCard(result)

                    // Clinical alert card (separated for visual emphasis)
                    if (result.alert.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AlertCard(result.alert)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                DisclaimerCard()
            }
        }

        // ── Bottom Floating Pill Buttons ───────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
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

            // Secondary button: correct data (only for validation errors)
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

// ─────────────────────────────────────────────────────────────────────────────
// Hero Header - Success
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SuccessHeader(result: DosageResult.Success, drugLabel: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-30).dp)
                .background(Color.White.copy(alpha = 0.07f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-20).dp, y = 30.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )

        Column {
            // Success icon + drug label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint               = Color.White.copy(alpha = 0.8f),
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text  = drugLabel ?: "Dose Calcolata",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total dose — prominent number
            Text(
                text       = formatDose(result.totalDose),
                fontSize   = 54.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                lineHeight = 60.sp
            )
            Text(
                text  = result.unit,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.85f)
            )

            // Capped to max dose alert
            if (result.cappedToMaxDose) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFFFD080).copy(alpha = 0.2f)
                ) {
                    Text(
                        text     = "⚠ Ridotta al massimo consentito",
                        style    = MaterialTheme.typography.labelLarge,
                        color    = Color(0xFFFFD080),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero Header - Error (validation and generic)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ErrorHeader(title: String, message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Filled.Warning,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.error,
                    modifier           = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text  = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Detail card: formula and bibliographic source
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DetailsCard(result: DosageResult.Success) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text  = "Formula applicata",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text  = result.formula,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (result.source.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text      = result.source,
                    style     = MaterialTheme.typography.labelSmall,
                    color     = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Clinical alert card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AlertCard(alert: String) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text     = "⚠",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text  = "Avviso clinico",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = alert,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Disclaimer card (always visible)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DisclaimerCard() {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = "DISCLAIMER",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text  = "Strumento a finalità esclusivamente didattiche. " +
                        "Non sostituisce la valutazione clinica del medico. " +
                        "Verificare sempre il dosaggio sulla scheda tecnica ufficiale (RCP/AIFA).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Utility
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Formats the dose: without decimals if it's an integer (3600 instead of 3600.0),
 * otherwise 2 significant decimals.
 */
private fun formatDose(dose: Double): String =
    if (dose == dose.toLong().toDouble()) dose.toLong().toString()
    else String.format(Locale.US, "%.2f", dose)
