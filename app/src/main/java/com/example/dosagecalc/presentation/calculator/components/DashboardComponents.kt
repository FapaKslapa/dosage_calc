package com.example.dosagecalc.presentation.calculator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.dosagecalc.domain.model.Drug
import com.example.dosagecalc.domain.model.FormulaType

private fun FormulaType.labelIt(): String = when (this) {
    FormulaType.PER_KG   -> "per kg"
    FormulaType.PER_M2   -> "per m²"
    FormulaType.FIXED    -> "dose fissa"
    FormulaType.BY_RANGE -> "per fascia"
}

private enum class ShortcutColorVariant { PRIMARY, SECONDARY, TERTIARY, ERROR }

private data class ShortcutItem(
    val icon: ImageVector,
    val iconDescription: String,
    val title: String,
    val subtitle: String,
    val variant: ShortcutColorVariant,
    val shape: Shape,
    val onClick: () -> Unit
)

private data class ResolvedShortcutColors(
    val container: Color,
    val iconBg: Color,
    val iconTint: Color,
    val text: Color
)

private fun resolveColors(variant: ShortcutColorVariant, cs: ColorScheme) = when (variant) {
    ShortcutColorVariant.PRIMARY   -> ResolvedShortcutColors(cs.primaryContainer,   cs.primary,   cs.onPrimary,   cs.onPrimaryContainer)
    ShortcutColorVariant.SECONDARY -> ResolvedShortcutColors(cs.secondaryContainer, cs.secondary, cs.onSecondary, cs.onSecondaryContainer)
    ShortcutColorVariant.TERTIARY  -> ResolvedShortcutColors(cs.tertiaryContainer,  cs.tertiary,  cs.onTertiary,  cs.onTertiaryContainer)
    ShortcutColorVariant.ERROR     -> ResolvedShortcutColors(cs.errorContainer,     cs.error,     cs.onError,     cs.onErrorContainer)
}

@Composable
fun DashboardShortcuts(
    onNavigateToPatients: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToReminders: () -> Unit = {},
    onNavigateToAddData: () -> Unit = {}
) {
    val items = listOf(
        ShortcutItem(Icons.Filled.Person,              "Pazienti",   "Pazienti",   "Anagrafica",        ShortcutColorVariant.SECONDARY, RoundedCornerShape(topStart = 8.dp,  topEnd = 32.dp, bottomEnd = 8.dp,  bottomStart = 32.dp), onNavigateToPatients),
        ShortcutItem(Icons.AutoMirrored.Filled.List,   "Storico",    "Storico",    "Calcoli Passati",   ShortcutColorVariant.TERTIARY,  RoundedCornerShape(16.dp),                                                                    onNavigateToHistory),
        ShortcutItem(Icons.Filled.DateRange,           "Calendario", "Calendario", "Promemoria Attivi", ShortcutColorVariant.PRIMARY,   RoundedCornerShape(16.dp),                                                                    onNavigateToReminders),
        ShortcutItem(Icons.Rounded.Add,                "Nuovo",      "Nuovo",      "Aggiungi Farmaco",  ShortcutColorVariant.ERROR,     RoundedCornerShape(topStart = 32.dp, topEnd = 8.dp,  bottomEnd = 32.dp, bottomStart = 8.dp),  onNavigateToAddData)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .offset(y = (-30).dp)
    ) {
        items.forEachIndexed { index, item ->
            ShortcutCard(item)
            Spacer(modifier = Modifier.width(if (index < items.lastIndex) 16.dp else 4.dp))
        }
    }
}

@Composable
private fun ShortcutCard(item: ShortcutItem) {
    val colors = resolveColors(item.variant, MaterialTheme.colorScheme)
    Card(
        modifier = Modifier.width(140.dp).height(110.dp),
        onClick = item.onClick,
        shape = item.shape,
        colors = CardDefaults.cardColors(containerColor = colors.container),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.background(colors.iconBg, CircleShape).padding(8.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.iconDescription,
                    tint = colors.iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(item.title,    style = MaterialTheme.typography.titleMedium, color = colors.text)
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall,   color = colors.text.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun DrugSelectionCard(
    drug: Drug,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        modifier = Modifier
            .width(260.dp)
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = drug.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = drug.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = drug.unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                
                if (onEditClick != null) {
                    androidx.compose.material3.IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Modifica",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (onDeleteClick != null) {
                    androidx.compose.material3.IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Elimina",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = drug.indication,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = drug.formulaType.labelIt(),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DrugPreviewCard(drug: Drug) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Dettagli Clinici",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Dosaggio Base",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text  = "${drug.unitDose} ${drug.unit} (${drug.formulaType.labelIt()})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (drug.alert.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Attenzione",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text  = drug.alert,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
