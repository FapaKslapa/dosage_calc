package com.example.dosagecalc.presentation.patient.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
    onEditClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "patient_card_scale"
    )
    val sp = MaterialTheme.spacing
    val shapes = LocalDosageShapes.current
    val cs = MaterialTheme.colorScheme

    ExpressiveCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
    ) {
        Column(modifier = Modifier.padding(horizontal = sp.lg, vertical = sp.base)) {

            Text(
                text = "${patient.surname}, ${patient.name}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                ),
                color = cs.onSurface
            )

            Spacer(modifier = Modifier.height(sp.sm))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                Surface(shape = shapes.chip, color = cs.secondaryContainer) {
                    Text(
                        text = "${patient.weightKg} kg",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(horizontal = sp.sm, vertical = sp.xs),
                        color = cs.onSecondaryContainer
                    )
                }
                if (patient.heightCm != null) {
                    Surface(shape = shapes.chip, color = cs.tertiaryContainer) {
                        Text(
                            text = "${patient.heightCm} cm",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(horizontal = sp.sm, vertical = sp.xs),
                            color = cs.onTertiaryContainer
                        )
                    }
                }
                Surface(shape = shapes.chip, color = cs.surfaceVariant) {
                    Text(
                        text = "${patient.ageYears} aa",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(horizontal = sp.sm, vertical = sp.xs),
                        color = cs.onSurfaceVariant
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
                            border = BorderStroke(0.5.dp, cs.error.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = sp.sm, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.Warning,
                                    contentDescription = null,
                                    tint = cs.error,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "IRC",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = cs.error
                                )
                            }
                        }
                    }
                    if (patient.hasHepaticImpairment) {
                        Surface(
                            shape = shapes.chip,
                            color = cs.tertiaryContainer.copy(alpha = 0.7f),
                            border = BorderStroke(0.5.dp, cs.tertiary.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = sp.sm, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.Warning,
                                    contentDescription = null,
                                    tint = cs.tertiary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Epat.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = cs.tertiary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(sp.sm))
            HorizontalDivider(
                color = cs.outlineVariant,
                thickness = 0.5.dp
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    shape = shapes.chip
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Modifica",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Modifica",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                TextButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    shape = shapes.chip,
                    colors = ButtonDefaults.textButtonColors(contentColor = cs.error)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Elimina",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Elimina",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
