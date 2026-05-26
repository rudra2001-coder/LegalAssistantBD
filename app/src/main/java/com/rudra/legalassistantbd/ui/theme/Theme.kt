package com.rudra.legalassistantbd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DarkBackground,
    primaryContainer = GoldDark,
    onPrimaryContainer = White,
    secondary = GoldLight,
    onSecondary = DarkBackground,
    tertiary = GoldVariant,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = WhiteSoft,
    surface = DarkSurface,
    onSurface = WhiteSoft,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = GrayLight,
    outline = GrayDark,
    error = ErrorRed,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = White,
    primaryContainer = GoldLight,
    onPrimaryContainer = DarkBackground,
    secondary = Gold,
    onSecondary = White,
    tertiary = GoldVariant,
    onTertiary = White,
    background = WhiteSoft,
    onBackground = DarkBackground,
    surface = White,
    onSurface = DarkBackground,
    surfaceVariant = WhiteSoft,
    onSurfaceVariant = GrayDark,
    outline = GrayLight,
    error = ErrorRed,
    onError = White
)

@Composable
fun LegalAssistantBDTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LegalTypography,
        content = content
    )
}
