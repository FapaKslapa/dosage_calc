package com.example.dosagecalc.presentation.ui.util

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("WindowSizeClass not provided — wrap root content with CompositionLocalProvider")
}

/** True when height is Compact, i.e. phone in landscape. Use to compress headers. */
@Composable
fun isCompactHeight(): Boolean =
    LocalWindowSizeClass.current.heightSizeClass == WindowHeightSizeClass.Compact

/** True on tablets (portrait or landscape) and large foldables. */
@Composable
fun isMediumOrExpandedWidth(): Boolean =
    LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact

/** True only on tablets in landscape (>840 dp). Use for 2-column layouts. */
@Composable
fun isExpandedWidth(): Boolean =
    LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Expanded

/**
 * Caps composable width so forms and detail screens don't stretch edge-to-edge on tablets.
 * On compact phones this is a no-op (phone width < maxWidth).
 */
fun Modifier.responsiveContentWidth(maxWidth: Dp = 640.dp): Modifier =
    this.widthIn(max = maxWidth)
