package com.example.dosagecalc.presentation.calculator.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dosagecalc.domain.model.DosageResult
import com.example.dosagecalc.presentation.ui.components.GradientScreenHeader
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.LocalElevation
import com.example.dosagecalc.presentation.ui.theme.spacing
import com.example.dosagecalc.presentation.ui.util.isCompactHeight
import java.util.Locale

@Composable
fun SuccessHeader(result: DosageResult.Success, drugLabel: String?) {
    var startAnimation by remember(result.totalDose) { mutableStateOf(false) }
    val animatedFraction by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        label = "dose_count_up"
    )
    LaunchedEffect(result.totalDose) { startAnimation = true }

    val displayDose = result.totalDose * animatedFraction.toDouble()
    val displayDoseMax = result.totalDoseMax?.let { it * animatedFraction.toDouble() }

    val compact = isCompactHeight()
    val sp = MaterialTheme.spacing
    val shapes = LocalDosageShapes.current
    val verticalPad = if (compact) sp.md else sp.xl
    val doseFontSize = if (compact) 36.sp else 54.sp
    val doseFontLineHeight = if (compact) 42.sp else 60.sp

    GradientScreenHeader(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.padding(bottom = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = sp.xl, vertical = verticalPad)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onPrimary,
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(sp.sm))
                Text(
                    text  = drugLabel ?: "Dose Calcolata",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(if (compact) sp.sm else sp.base))

            val rangeText = if (displayDoseMax != null) {
                "${formatDose(displayDose)} - ${formatDose(displayDoseMax)}"
            } else {
                formatDose(displayDose)
            }

            Text(
                text       = rangeText,
                fontSize   = doseFontSize,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.onPrimary,
                lineHeight = doseFontLineHeight
            )
            Text(
                text  = result.unit,
                style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )

            if (result.cappedToMaxDose) {
                Spacer(modifier = Modifier.height(if (compact) sp.sm else sp.base))
                Surface(
                    shape = shapes.pill,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text     = "⚠ Ridotta al massimo consentito",
                        style    = MaterialTheme.typography.labelLarge,
                        color    = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = sp.base, vertical = sp.sm)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorHeader(title: String, message: String) {
    val compact = isCompactHeight()
    val sp = MaterialTheme.spacing
    GradientScreenHeader(
        colors = listOf(
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = sp.xl, vertical = if (compact) sp.md else sp.xl)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Filled.Warning,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onError,
                    modifier           = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(sp.md))
                Text(
                    text  = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onError
                )
            }
            Spacer(modifier = Modifier.height(sp.md))
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun DetailsCard(result: DosageResult.Success) {
    val sp = MaterialTheme.spacing
    val shapes = LocalDosageShapes.current
    val elev = LocalElevation.current
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = shapes.card,
        elevation = CardDefaults.cardElevation(defaultElevation = elev.level2),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(sp.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, shapes.field),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "fx",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(sp.base))
                Column {
                    Text(
                        text  = "Formula applicata",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(sp.xs))
                    Text(
                        text  = result.formula,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (result.totalCycleDose != null || result.totalTherapyDose != null) {
                Spacer(modifier = Modifier.height(sp.lg))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(sp.lg))

                Column {
                    result.totalCycleDose?.let { cycleDose ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(sp.md))
                            Column {
                                Text(
                                    text = "Dose Totale per Ciclo",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${formatDose(cycleDose)} ${result.unit}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    if (result.totalCycleDose != null && result.totalTherapyDose != null) {
                        Spacer(modifier = Modifier.height(sp.base))
                    }

                    result.totalTherapyDose?.let { therapyDose ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(sp.md))
                            Column {
                                Text(
                                    text = "Dose Totale Terapia",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${formatDose(therapyDose)} ${result.unit}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }

            if (result.source.isNotBlank()) {
                Spacer(modifier = Modifier.height(sp.lg))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(sp.lg))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(sp.sm))
                    Text(
                        text      = result.source,
                        style     = MaterialTheme.typography.bodyMedium,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun AlertCard(alert: String) {
    val sp = MaterialTheme.spacing
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = LocalDosageShapes.current.card,
        elevation = CardDefaults.cardElevation(defaultElevation = LocalElevation.current.level2),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier          = Modifier.padding(sp.lg),
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
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(sp.xs))
                Text(
                    text  = alert,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun DisclaimerCard() {
    val sp = MaterialTheme.spacing
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = LocalDosageShapes.current.card,
        elevation = CardDefaults.cardElevation(defaultElevation = LocalElevation.current.level2),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(sp.lg)) {
            Text(
                text  = "DISCLAIMER",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(sp.xs + 2.dp))
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

private fun formatDose(dose: Double): String =
    if (dose == dose.toLong().toDouble()) dose.toLong().toString()
    else String.format(Locale.US, "%.2f", dose)
