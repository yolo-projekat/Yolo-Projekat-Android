package com.yolo.vozilo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ThemeBlue = Color(0xFF3498DB)
val ThemeAlert = Color(0xFFE74C3C)
val ThemeSuccess = Color(0xFF2ECC71)

@Composable
fun VoziloTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(primary = ThemeBlue, background = Color(0xFF121212), surface = Color(0xFF1E1E1E), onSurface = Color.White)
    } else {
        lightColorScheme(primary = ThemeBlue, background = Color(0xFFFDFDFD), surface = Color(0xFFF2F9FF), onSurface = Color.Black)
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}