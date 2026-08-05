package dev.exchequer.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ExchequerColors = darkColorScheme(
    primary = Color(0xFFD7C6FF),
    onPrimary = Color(0xFF24183A),
    secondary = Color(0xFF8DE8FF),
    onSecondary = Color(0xFF08282F),
    background = Color(0xFF07070A),
    onBackground = Color(0xFFF4F1FA),
    surface = Color(0xFF121119),
    onSurface = Color(0xFFF4F1FA),
    surfaceVariant = Color(0xFF1A1822),
    onSurfaceVariant = Color(0xFFC9C3D5),
    error = Color(0xFFFFB4AB),
)

@Composable
fun ExchequerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ExchequerColors,
        typography = Typography(),
        content = content,
    )
}
