package com.example.dosagecalc.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Schema colori chiaro (default)
private val LightColorScheme = lightColorScheme(
    primary          = Blue40,
    onPrimary        = Neutral99,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,

    secondary        = Teal40,
    onSecondary      = Neutral99,
    secondaryContainer = Teal90,
    onSecondaryContainer = Blue10,

    error            = Error40,
    errorContainer   = Error90,

    background       = Neutral99,
    onBackground     = Neutral10,
    surface          = Neutral99,
    onSurface        = Neutral10,
    surfaceVariant   = Neutral90,
)

// Schema colori scuro
private val DarkColorScheme = darkColorScheme(
    primary          = Blue80,
    onPrimary        = Blue20,
    primaryContainer = Blue10,
    onPrimaryContainer = Blue90,

    secondary        = Teal80,
    onSecondary      = Blue10,
    secondaryContainer = Teal40,
    onSecondaryContainer = Teal90,

    error            = Error90,
    errorContainer   = Error40,

    background       = Neutral10,
    onBackground     = Neutral90,
    surface          = Neutral10,
    onSurface        = Neutral90,
)

/**
 * Tema principale dell'app.
 *
 * Supporta Dynamic Color (Android 12+) per adattarsi al wallpaper dell'utente,
 * con fallback al tema medicale blu-teal su versioni precedenti.
 */
@Composable
fun DosageCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disponibile solo su Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = DosageCalcTypography,
        content     = content
    )
}
