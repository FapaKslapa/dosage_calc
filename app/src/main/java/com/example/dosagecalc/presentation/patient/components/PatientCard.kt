package com.example.dosagecalc.presentation.patient.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.ui.unit.sp
import com.example.dosagecalc.domain.model.Patient
import com.example.dosagecalc.presentation.ui.components.ExpressiveCard
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.spacing

@Composable
fun PatientCard(
    patient: Patient,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit = {},
) {
    val sp = MaterialTheme.spacing
    val shapes = LocalDosageShapes.current
    val cs = MaterialTheme.colorScheme

    ExpressiveCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = sp.lg, vertical = sp.base)) {
            Text(
                text = "${patient.surname}, ${patient.name}",
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                    ),
                color = cs.onSurface,
            )

            Spacer(modifier = Modifier.height(sp.sm))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                Surface(shape = shapes.chip, color = cs.secondaryContainer) {
                    Text(
                        text = "${patient.weightKg} kg",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(horizontal = sp.sm, vertical = sp.xs),
                        color = cs.onSecondaryContainer,
                    )
                }
                if (patient.heightCm != null) {
                    Surface(shape = shapes.chip, color = cs.tertiaryContainer) {
                        Text(
                            text = "${patient.heightCm} cm",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(horizontal = sp.sm, vertical = sp.xs),
                            color = cs.onTertiaryContainer,
                        )
                    }
                }
                Surface(shape = shapes.chip, color = cs.surfaceVariant) {
                    Text(
                        text = "${patient.ageYears} aa",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(horizontal = sp.sm, vertical = sp.xs),
                        color = cs.onSurfaceVariant,
                    )
                }
            }

            if (patient.hasRenalImpairment || patient.hasHepaticImpairment) {
                Spacer(modifier = Modifier.height(sp.xs))
                Row(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                    if (patient.hasRenalImpairment) {
                        Surface(
                            shape = shapes.chip,
                            color = cs.errorContainer.copy(alpha = 0.55f),
                            border = BorderStroke(0.5.dp, cs.error.copy(alpha = 0.4f)),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = sp.sm, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Rounded.Warning,
                                    contentDescription = null,
                                    tint = cs.error,
                                    modifier = Modifier.size(11.dp),
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "IRC",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = cs.error,
                                )
                            }
                        }
                    }
                    if (patient.hasHepaticImpairment) {
                        Surface(
                            shape = shapes.chip,
                            color = cs.tertiaryContainer.copy(alpha = 0.7f),
                            border = BorderStroke(0.5.dp, cs.tertiary.copy(alpha = 0.4f)),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = sp.sm, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Rounded.Warning,
                                    contentDescription = null,
                                    tint = cs.tertiary,
                                    modifier = Modifier.size(11.dp),
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Epat.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = cs.tertiary,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(sp.sm))
            HorizontalDivider(
                color = cs.outlineVariant,
                thickness = 0.5.dp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(sp.xs, Alignment.End),
            ) {
                Surface(
                    onClick = onEditClick,
                    shape = shapes.chip,
                    color = cs.surface,
                    border = BorderStroke(1.dp, cs.outlineVariant),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = sp.sm, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Modifica", tint = cs.primary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Modifica", style = MaterialTheme.typography.labelSmall, color = cs.primary)
                    }
                }
                Surface(
                    onClick = onDeleteClick,
                    shape = shapes.chip,
                    color = cs.errorContainer.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, cs.error.copy(alpha = 0.45f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = sp.sm, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Elimina", tint = cs.error, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Elimina", style = MaterialTheme.typography.labelSmall, color = cs.error)
                    }
                }
            }
        }
    }
}
