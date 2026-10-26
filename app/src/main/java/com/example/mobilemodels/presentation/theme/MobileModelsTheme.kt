package com.example.mobilemodels.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColors = lightColorScheme(
    primary = Color(0xFF1759D1),
    onPrimary = Color.White,
    secondary = Color(0xFF3478F6),
    background = Color(0xFFF5F8FD),
    surface = Color.White,
    error = Color(0xFFE5484D)
)

@Composable
fun MobileModelsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColors, content = content)
}
