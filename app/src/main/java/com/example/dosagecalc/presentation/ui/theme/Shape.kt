package com.example.dosagecalc.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val DosageMaterialShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

data class DosageShapes(
    val pill:             Shape = RoundedCornerShape(50),
    val field:            Shape = RoundedCornerShape(16.dp),
    val chip:             Shape = RoundedCornerShape(50),
    val tile:             Shape = RoundedCornerShape(16.dp),
    val card:             Shape = RoundedCornerShape(24.dp),
    val cardLarge:        Shape = RoundedCornerShape(28.dp),
    val sheet:            Shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    val heroBottom:       Shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
    val heroLanding:      Shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp),
    val expressive:       Shape = RoundedCornerShape(topStart = 32.dp, topEnd = 8.dp, bottomEnd = 32.dp, bottomStart = 8.dp),
    val expressiveMirror: Shape = RoundedCornerShape(topStart = 8.dp, topEnd = 32.dp, bottomEnd = 8.dp, bottomStart = 32.dp)
)

val LocalDosageShapes = staticCompositionLocalOf { DosageShapes() }

val MaterialTheme.dosageShapes: DosageShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalDosageShapes.current
