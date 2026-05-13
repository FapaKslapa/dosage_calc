package com.example.dosagecalc.presentation.calculator.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dosagecalc.presentation.ui.util.responsiveContentWidth
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.presentation.calculator.AddDataViewModel
import com.example.dosagecalc.presentation.ui.components.GradientScreenHeader
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.LocalElevation
import com.example.dosagecalc.presentation.ui.theme.spacing

@Composable
fun DrugDetailScreen(
    drugId: String,
    viewModel: AddDataViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var drug by remember { mutableStateOf<Drug?>(null) }
    var card1Visible by remember { mutableStateOf(false) }
    var card2Visible by remember { mutableStateOf(false) }
    var card3Visible by remember { mutableStateOf(false) }

    LaunchedEffect(drugId) {
        viewModel.loadDrug(drugId) { loadedDrug ->
            drug = loadedDrug
        }
    }
    LaunchedEffect(drug) {
        if (drug != null) {
            card1Visible = true
            delay(80); card2Visible = true
            delay(80); card3Visible = true
        }
    }

    val sp = MaterialTheme.spacing
    val shapes = LocalDosageShapes.current
    val cardEnter = fadeIn(tween(250)) + slideInVertically(
        initialOffsetY = { it / 4 },
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GradientScreenHeader(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.padding(bottom = 0.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = sp.sm)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro",
                                tint               = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text  = "Scheda Tecnica",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }

                    Column(modifier = Modifier.padding(start = sp.xl, end = sp.xl, top = sp.xs)) {
                        Text(
                            text  = drug?.name ?: "Caricamento...",
                            style = MaterialTheme.typography.displayMedium.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        drug?.category?.let { category ->
                            Surface(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                shape = shapes.pill,
                                modifier = Modifier.padding(top = sp.sm)
                            ) {
                                Text(
                                    text = category.label,
                                    modifier = Modifier.padding(horizontal = sp.md, vertical = sp.xs),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(sp.xl))
                    }
                }
            }

            if (drug != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(sp.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Column(modifier = Modifier.responsiveContentWidth(maxWidth = 720.dp)) {
                    AnimatedVisibility(visible = card1Visible, enter = cardEnter) {
                        InfoSection(
                            title = "Indicazione",
                            content = drug!!.indication,
                            icon = Icons.Default.Info
                        )
                    }

                    Spacer(modifier = Modifier.height(sp.base))

                    AnimatedVisibility(visible = card2Visible, enter = cardEnter) {
                        DetailCard(drug = drug!!)
                    }

                    drug!!.alert.let { alert ->
                        if (alert.isNotBlank()) {
                            Spacer(modifier = Modifier.height(sp.base))
                            AnimatedVisibility(visible = card3Visible, enter = cardEnter) {
                                WarningSection(title = "Avvertenze", content = alert)
                            }
                        }
                    }

                    drug!!.contraindications?.let { contra ->
                        Spacer(modifier = Modifier.height(sp.base))
                        AnimatedVisibility(visible = card3Visible, enter = cardEnter) {
                            WarningSection(title = "Controindicazioni", content = contra)
                        }
                    }

                    drug!!.sideEffects?.let { side ->
                        Spacer(modifier = Modifier.height(sp.base))
                        AnimatedVisibility(visible = card3Visible, enter = cardEnter) {
                            InfoSection(
                                title = "Effetti Collaterali",
                                content = side,
                                icon = Icons.Default.Warning,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(sp.xxl))

                    Text(
                        text = "Fonte: ${drug!!.source}",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(sp.xxxl))
                }
                }
            }
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    content: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    val sp = MaterialTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = LocalDosageShapes.current.card,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = LocalElevation.current.level2)
    ) {
        Column(modifier = Modifier.padding(sp.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(sp.sm))
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = color)
            }
            Spacer(modifier = Modifier.height(sp.sm))
            Text(text = content, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun WarningSection(title: String, content: String) {
    val sp = MaterialTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = LocalDosageShapes.current.card,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = LocalElevation.current.level2)
    ) {
        Column(modifier = Modifier.padding(sp.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(sp.sm))
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(sp.sm))
            Text(text = content, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}

@Composable
fun DetailCard(drug: Drug) {
    val sp = MaterialTheme.spacing
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = LocalDosageShapes.current.card,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = LocalElevation.current.level2)
    ) {
        Column(modifier = Modifier.padding(sp.lg)) {
            Text(text = "Dosaggio e Formula", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(sp.base))

            DetailRow(label = "Dose Unitaria", value = "${drug.unitDose} ${drug.unit}")
            HorizontalDivider(modifier = Modifier.padding(vertical = sp.md), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            DetailRow(label = "Tipo Formula", value = drug.formulaType.name.replace("_", " "))

            if (drug.daysPerCycle != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = sp.md), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                DetailRow(label = "Giorni Ciclo", value = drug.daysPerCycle.toString())
            }

            if (drug.numberOfCycles != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = sp.md), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                DetailRow(label = "Numero Cicli", value = drug.numberOfCycles.toString())
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
