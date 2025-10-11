package com.karlof002.quran.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Theme Colors - Softer Islamic Green Theme (Eye-friendly)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00796B), // Darker, more visible green for navigation
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0F2F1), // Much softer green container
    onPrimaryContainer = Color(0xFF004D40), // Darker text for better contrast
    secondary = Color(0xFF4A635F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8F5E8), // Soft green-tinted container
    onSecondaryContainer = Color(0xFF2E3B37), // Darker text
    tertiary = Color(0xFF456179),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE3F2FD), // Soft blue container
    onTertiaryContainer = Color(0xFF1B2631), // Darker text
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFCFCFC), // Slightly off-white, easier on eyes
    onBackground = Color(0xFF1C1B1F), // Soft dark gray instead of pure black
    surface = Color(0xFFFCFCFC), // Slightly off-white
    onSurface = Color(0xFF1C1B1F), // Soft dark gray
    surfaceVariant = Color(0xFFF3F4F6), // Very light gray with slight warmth
    onSurfaceVariant = Color(0xFF44464F), // Softer dark gray
    outline = Color(0xFFE5E7EB), // Light gray borders
    inverseOnSurface = Color(0xFFF4EFF4),
    inverseSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF4DB6AC),
    surfaceTint = Color(0xFF00796B), // Match primary for consistency
    outlineVariant = Color(0xFFF1F3F4), // Very subtle borders
    scrim = Color(0xFF000000)
)

// Dark Theme Colors - Islamic Green Theme with softer dark mode
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4DB6AC),  // Lighter teal for better visibility in dark mode
    onPrimary = Color(0xFF003D36),
    primaryContainer = Color(0xFF00695C),
    onPrimaryContainer = Color(0xFFB2DFDB),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003D36),
    secondaryContainer = Color(0xFF2A3533),
    onSecondaryContainer = Color(0xFFCDE8E2),
    tertiary = Color(0xFF7FA3C9),
    onTertiary = Color(0xFF003D36),
    tertiaryContainer = Color(0xFF2C3E4F),
    onTertiaryContainer = Color(0xFFCCE5FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A1C1E),  // Soft dark gray instead of pure black
    onBackground = Color(0xFFE3E2E6),  // Soft white
    surface = Color(0xFF1A1C1E),  // Soft dark gray
    onSurface = Color(0xFFE3E2E6),  // Soft white
    surfaceVariant = Color(0xFF2B2D30),  // Slightly lighter gray for cards
    onSurfaceVariant = Color(0xFFC4C6D0),  // Muted white
    outline = Color(0xFF3C3F41),  // Subtle borders
    inverseOnSurface = Color(0xFF1A1C1E),
    inverseSurface = Color(0xFFE3E2E6),
    inversePrimary = Color(0xFF00695C),
    surfaceTint = Color(0xFF4DB6AC),
    outlineVariant = Color(0xFF3C3F41),
    scrim = Color(0xFF000000)
)

@Composable
fun QuranTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
