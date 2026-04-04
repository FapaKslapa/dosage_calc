package com.example.dosagecalc.presentation.calculator.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dosagecalc.domain.model.DrugInteraction
import com.example.dosagecalc.domain.model.InteractionRiskLevel

@Composable
fun InteractionsCard(interactions: List<DrugInteraction>) {
    if (interactions.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Interazioni Rilevate (${interactions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            interactions.forEach { interaction ->
                val riskColor = when (interaction.riskLevel) {
                    InteractionRiskLevel.HIGH -> MaterialTheme.colorScheme.error
                    InteractionRiskLevel.MODERATE -> MaterialTheme.colorScheme.tertiary
                    InteractionRiskLevel.LOW -> MaterialTheme.colorScheme.secondary
                }
                
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = riskColor,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rischio ${interaction.riskLevel.label}",
                            style = MaterialTheme.typography.labelLarge,
                            color = riskColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = interaction.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (interaction != interactions.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}
