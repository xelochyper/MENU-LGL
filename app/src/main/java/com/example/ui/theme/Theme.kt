package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LglDarkColorScheme = darkColorScheme(
    primary = ElegantLavenderPrimary,
    onPrimary = Color(0xFF381E72),
    primaryContainer = ElegantPurpleContainer,
    onPrimaryContainer = ElegantLavenderPrimary,
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    tertiary = WarningOrange,
    background = ElegantDarkBg,
    onBackground = ElegantTextMain,
    surface = ElegantCardSurface,
    onSurface = ElegantTextMain,
    surfaceVariant = ElegantHeaderSurface,
    onSurfaceVariant = ElegantTextMuted,
    outline = ElegantBorder,
    error = ElegantBanRed,
    onError = ElegantBanRedContainer
)

@Composable
fun LglModMenuTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LglDarkColorScheme,
        typography = Typography,
        content = content
    )
}
