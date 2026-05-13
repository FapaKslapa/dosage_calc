package com.example.dosagecalc.presentation.calculator.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.LocalElevation
import com.example.dosagecalc.presentation.ui.theme.spacing

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
    modifier: Modifier = Modifier,
    onNavigateToPatients: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToReminders: () -> Unit = {},
    onNavigateToAddData: () -> Unit = {}
) {
    val shapes = LocalDosageShapes.current
    val items = listOf(
        ShortcutItem(Icons.Filled.Person,            "Pazienti",   "Pazienti",   "Anagrafica",        ShortcutColorVariant.SECONDARY, shapes.expressiveMirror, onNavigateToPatients),
        ShortcutItem(Icons.AutoMirrored.Filled.List, "Storico",    "Storico",    "Calcoli Passati",   ShortcutColorVariant.TERTIARY,  shapes.tile,             onNavigateToHistory),
        ShortcutItem(Icons.Filled.DateRange,         "Calendario", "Calendario", "Promemoria Attivi", ShortcutColorVariant.PRIMARY,   shapes.tile,             onNavigateToReminders),
        ShortcutItem(Icons.Rounded.Add,              "Nuovo",      "Nuovo",      "Aggiungi Farmaco",  ShortcutColorVariant.ERROR,     shapes.expressive,       onNavigateToAddData)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.lg)
    ) {
        items.forEachIndexed { index, item ->
            ShortcutCard(item)
            Spacer(modifier = Modifier.width(if (index < items.lastIndex) MaterialTheme.spacing.base else MaterialTheme.spacing.xs))
        }
    }
}

@Composable
private fun ShortcutCard(item: ShortcutItem) {
    val colors = resolveColors(item.variant, MaterialTheme.colorScheme)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "shortcut_scale"
    )

    Card(
        modifier = Modifier.width(140.dp).height(110.dp).scale(scale),
        onClick = item.onClick,
        interactionSource = interactionSource,
        shape = item.shape,
        colors = CardDefaults.cardColors(containerColor = colors.container),
        elevation = CardDefaults.cardElevation(defaultElevation = LocalElevation.current.level3)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.base),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier.background(colors.iconBg, CircleShape).padding(MaterialTheme.spacing.sm)
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
