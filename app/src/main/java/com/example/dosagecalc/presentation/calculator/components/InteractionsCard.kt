package com.example.dosagecalc.presentation.calculator.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dosagecalc.domain.model.DrugInteraction
import com.example.dosagecalc.domain.model.InteractionRiskLevel
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.LocalElevation
import com.example.dosagecalc.presentation.ui.theme.spacing

@Composable
fun InteractionsCard(interactions: List<DrugInteraction>) {
    if (interactions.isEmpty()) return

    val sp = MaterialTheme.spacing
    val shapes = LocalDosageShapes.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes.card,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = LocalElevation.current.level1),
    ) {
        Column(modifier = Modifier.padding(sp.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.width(sp.md))
                Text(
                    text = "Interazioni Rilevate (${interactions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(sp.md))

            interactions.forEach { interaction ->
                val riskColor =
                    when (interaction.riskLevel) {
                        InteractionRiskLevel.HIGH -> MaterialTheme.colorScheme.error
                        InteractionRiskLevel.MODERATE -> MaterialTheme.colorScheme.tertiary
                        InteractionRiskLevel.LOW -> MaterialTheme.colorScheme.secondary
                    }

                Column(modifier = Modifier.padding(vertical = sp.sm)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = riskColor,
                            shape = shapes.chip,
                            modifier = Modifier.size(8.dp),
                        ) {}
                        Spacer(modifier = Modifier.width(sp.sm))
                        Text(
                            text = "Rischio ${interaction.riskLevel.label}",
                            style = MaterialTheme.typography.labelLarge,
                            color = riskColor,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        text = interaction.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(top = sp.xs),
                    )
                }
                if (interaction != interactions.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = sp.sm),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    )
                }
            }
        }
    }
}
