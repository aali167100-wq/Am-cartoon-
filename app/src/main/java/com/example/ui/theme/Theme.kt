package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StudioDarkColorScheme = darkColorScheme(
    primary = StudioPrimary,
    secondary = StudioSecondary,
    tertiary = StudioAccent,
    background = StudioDarkBg,
    surface = StudioSurface,
    surfaceVariant = StudioCard,
    onPrimary = StudioDarkBg,
    onSecondary = StudioDarkBg,
    onBackground = StudioTextMain,
    onSurface = StudioTextMain,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioBorder
)

@Composable
fun AMCartoonTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StudioDarkColorScheme,
        typography = Typography,
        content = content
    )
}
