package com.example.dosagecalc.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dosagecalc.domain.model.HepaticStage
import com.example.dosagecalc.domain.model.RenalStage

@Composable
fun ImpairmentChipsRow(
    renalStage: RenalStage,
    hepaticStage: HepaticStage,
    onRenalStageChanged: (RenalStage) -> Unit,
    onHepaticStageChanged: (HepaticStage) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Funzione Renale (CKD/GFR)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RenalStage.entries.forEach { stage ->
                FilterChip(
                    selected = renalStage == stage,
                    onClick = { onRenalStageChanged(stage) },
                    label = { Text(stage.label) },
                    leadingIcon = if (renalStage == stage && stage != RenalStage.NONE) {
                        { Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (stage == RenalStage.NONE) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    )
                )
            }
        }

        Text(
            text = "Funzione Epatica (Child-Pugh)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HepaticStage.entries.forEach { stage ->
                FilterChip(
                    selected = hepaticStage == stage,
                    onClick = { onHepaticStageChanged(stage) },
                    label = { Text(stage.label) },
                    leadingIcon = if (hepaticStage == stage && stage != HepaticStage.NONE) {
                        { Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (stage == HepaticStage.NONE) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    )
                )
            }
        }
    }
}
