package com.example.dosagecalc.presentation.history.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dosagecalc.domain.model.HistoryRecord
import com.example.dosagecalc.presentation.ui.components.ExpressiveCard
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.spacing
import java.time.format.DateTimeFormatter

@Composable
fun HistoryCard(
    record: HistoryRecord,
    onDeleteClick: () -> Unit,
    mirrored: Boolean = false
) {
    val sp = MaterialTheme.spacing
    val shapes = LocalDosageShapes.current
    val cs = MaterialTheme.colorScheme

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM · HH:mm")
    val doseText = if (record.calculatedDoseMax != null) {
        "${fmt(record.calculatedDose)} – ${fmt(record.calculatedDoseMax)} ${record.doseUnit}"
    } else {
        "${fmt(record.calculatedDose)} ${record.doseUnit}"
    }

    ExpressiveCard(
        modifier  = Modifier.fillMaxWidth(),
        mirrored  = mirrored,
        containerAlpha = 0.9f
    ) {
        Column(
            modifier = Modifier.padding(sp.base),
            verticalArrangement = Arrangement.spacedBy(sp.xs)
        ) {
            Surface(
                shape = shapes.chip,
                color = cs.tertiary.copy(alpha = 0.10f)
            ) {
                Text(
                    text     = record.date.format(dateFormatter),
                    style    = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color    = cs.tertiary,
                    modifier = Modifier.padding(horizontal = sp.sm, vertical = 4.dp)
                )
            }

            Text(
                text  = record.drugName,
                style = MaterialTheme.typography.titleSmall.copy(fontFamily = FontFamily.Serif),
                color = cs.onSurface,
                maxLines = 2
            )

            Text(
                text  = doseText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = cs.tertiary,
                maxLines = 1
            )

            HorizontalDivider(
                color     = cs.outlineVariant.copy(alpha = 0.4f),
                thickness = 0.5.dp
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                InfoChip("${record.weightKg} kg", cs.secondary.copy(alpha = 0.10f), cs.secondary)
                InfoChip("${record.ageYears} aa", cs.primary.copy(alpha = 0.10f), cs.primary)
                record.heightCm?.let {
                    InfoChip("${it} cm", cs.tertiary.copy(alpha = 0.10f), cs.tertiary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape    = shapes.chip,
                    color    = cs.errorContainer.copy(alpha = 0.45f),
                    border   = BorderStroke(1.dp, cs.error.copy(alpha = 0.45f)),
                    modifier = Modifier.clickable { onDeleteClick() }
                ) {
                    Row(
                        modifier  = Modifier.padding(horizontal = sp.sm, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Elimina", tint = cs.error, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Elimina", style = MaterialTheme.typography.labelSmall, color = cs.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String, bg: androidx.compose.ui.graphics.Color, fg: androidx.compose.ui.graphics.Color) {
    val shapes = LocalDosageShapes.current
    val sp = MaterialTheme.spacing
    Surface(shape = shapes.chip, color = bg) {
        Text(
            text     = text,
            style    = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color    = fg,
            modifier = Modifier.padding(horizontal = sp.sm, vertical = 3.dp)
        )
    }
}

private fun fmt(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString()
    else String.format(java.util.Locale.US, "%.2f", value)
