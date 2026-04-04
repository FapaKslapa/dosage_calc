package com.example.dosagecalc.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun ImpairmentChipsRow(
    hasRenalImpairment: Boolean,
    hasHepaticImpairment: Boolean,
    onRenalChanged: (Boolean) -> Unit,
    onHepaticChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FilterChip(
            selected = hasRenalImpairment,
            onClick = { onRenalChanged(!hasRenalImpairment) },
            label = { Text("Insufficienza Renale") },
            leadingIcon = if (hasRenalImpairment) {
                { Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onErrorContainer
            )
        )
        FilterChip(
            selected = hasHepaticImpairment,
            onClick = { onHepaticChanged(!hasHepaticImpairment) },
            label = { Text("Insufficienza Epatica") },
            leadingIcon = if (hasHepaticImpairment) {
                { Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onErrorContainer
            )
        )
    }
}
