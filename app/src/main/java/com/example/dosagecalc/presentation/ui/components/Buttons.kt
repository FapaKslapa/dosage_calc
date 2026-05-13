package com.example.dosagecalc.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.LocalSpacing

@Composable
fun PillButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(56.dp),
        shape    = LocalDosageShapes.current.pill,
        enabled  = enabled
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector        = leadingIcon,
                contentDescription = null,
                modifier           = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(LocalSpacing.current.sm))
        }
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun OutlinedPillButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.height(56.dp),
        shape    = LocalDosageShapes.current.pill,
        border   = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        enabled  = enabled
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector        = leadingIcon,
                contentDescription = null,
                modifier           = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(LocalSpacing.current.sm))
        }
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}
