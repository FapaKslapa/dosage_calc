package com.example.dosagecalc.presentation.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary              = Purple40,
    onPrimary            = Color.White,
    primaryContainer     = Purple90,
    onPrimaryContainer   = Purple20,

    secondary            = Teal40,
    onSecondary          = Color.White,
    secondaryContainer   = Teal90,
    onSecondaryContainer = Teal30,

    tertiary             = Amber40,
    onTertiary           = Color.White,
    tertiaryContainer    = Amber90,
    onTertiaryContainer  = Color(0xFF3B2800),

    error                = Error40,
    onError              = Color.White,
    errorContainer       = Error90,
    onErrorContainer     = Color(0xFF410002),

    background           = WarmWhite,
    onBackground         = DarkInk,
    surface              = Color.White,
    onSurface            = DarkInk,
    surfaceVariant       = WarmGray,
    onSurfaceVariant     = MediumInk,
    outline              = Color(0xFFB8B0A6),
)

private val DarkColorScheme = darkColorScheme(
    primary              = Purple80,
    onPrimary            = Purple20,
    primaryContainer     = Purple40,
    onPrimaryContainer   = Purple90,

    secondary            = Teal80,
    onSecondary          = Color.White,
    secondaryContainer   = Teal40,
    onSecondaryContainer = Teal90,

    tertiary             = Amber80,
    onTertiary           = Color(0xFF3B2800),
    tertiaryContainer    = Amber40,
    onTertiaryContainer  = Amber90,

    error                = Error90,
    onError              = Color(0xFF690005),
    errorContainer       = Error40,
    onErrorContainer     = Error90,

    background           = DarkBackground,
    onBackground         = WarmWhite,
    surface              = DarkSurface,
    onSurface            = WarmWhite,
    surfaceVariant       = DarkSurfaceVariant,
    onSurfaceVariant     = WarmGray,
)

@Composable
fun DosageCalcTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
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
