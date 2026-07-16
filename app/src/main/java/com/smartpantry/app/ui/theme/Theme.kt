package com.smartpantry.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    primaryContainer = Color(0xFF1A2C4A),
    secondary = Color(0xFFCEC4D8),
    secondaryContainer = Color(0xFF312D3E),
    tertiary = Color(0xFFB8D8F0),
    tertiaryContainer = Color(0xFF1A2C4A),
    surface = Color(0xFF121212),
    background = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onPrimary = Color(0xFF00355E),
    onSecondary = Color(0xFF2D293B),
    onTertiary = Color(0xFF00355E),
    onSurface = Color(0xFFE6E1E5),
    onBackground = Color(0xFFE6E1E5),
    outline = Color(0xFF908C90),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A73E8),
    primaryContainer = Color(0xFFD6E4F0),
    secondary = Color(0xFF625B71),
    secondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFF006874),
    tertiaryContainer = Color(0xFFD6F4FF),
    surface = Color(0xFFFFFBFE),
    background = Color(0xFFFFFBFE),
    error = Color(0xFFBA1A1A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onSurface = Color(0xFF1C1B1F),
    onBackground = Color(0xFF1C1B1F),
    outline = Color(0xFF79747E),
)

@Composable
fun SmartPantryTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}