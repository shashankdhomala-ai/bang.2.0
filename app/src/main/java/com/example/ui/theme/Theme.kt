package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BangColorScheme = darkColorScheme(
    primary = BangPrimaryNeon,
    onPrimary = BangBackground,
    primaryContainer = BangSurfaceVariant,
    onPrimaryContainer = BangPrimaryNeon,
    secondary = BangCyanAccent,
    onSecondary = BangBackground,
    secondaryContainer = BangSurfaceVariant,
    onSecondaryContainer = BangCyanAccent,
    tertiary = BangAmberWarning,
    onTertiary = BangBackground,
    background = BangBackground,
    onBackground = BangTextPrimary,
    surface = BangSurface,
    onSurface = BangTextPrimary,
    surfaceVariant = BangSurfaceVariant,
    onSurfaceVariant = BangTextSecondary,
    outline = BangSurfaceBorder,
    error = BangSosRed,
    onError = BangTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // BANG defaults to high-contrast tactical dark
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = BangColorScheme,
        typography = Typography,
        content = content
    )
}

