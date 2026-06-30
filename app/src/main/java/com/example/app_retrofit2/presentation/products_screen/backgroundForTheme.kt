package com.example.app_retrofit2.presentation.products_screen

import androidx.compose.runtime.Composable
import com.example.app_retrofit2.R
import com.example.app_retrofit2.domain.model.ThemeMode

@Composable
fun backgroundForTheme(theme: ThemeMode): Int {
    return when (theme) {
        ThemeMode.SYSTEM -> R.drawable.background_system
        ThemeMode.LIGHT -> R.drawable.background_light
        ThemeMode.DARK -> R.drawable.background_dark
    }
}