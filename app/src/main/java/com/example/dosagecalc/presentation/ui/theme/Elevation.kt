package com.example.dosagecalc.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Elevation(
    val level0: Dp = 0.dp,
    val level1: Dp = 2.dp,
    val level2: Dp = 4.dp,
    val level3: Dp = 8.dp
)

val LocalElevation = staticCompositionLocalOf { Elevation() }

val MaterialTheme.elevation: Elevation
    @Composable
    @ReadOnlyComposable
    get() = LocalElevation.current

fun Modifier.softShadow(elevation: Dp, shape: Shape): Modifier = shadow(
    elevation    = elevation,
    shape        = shape,
    ambientColor = Color.Black.copy(alpha = 0.08f),
    spotColor    = Color.Black.copy(alpha = 0.18f)
)
