package com.example.app_retrofit2.presentation.products_screen

import androidx.datastore.preferences.core.Preferences
import com.example.app_retrofit2.domain.model.UserPreferences

data class ProductsUiState(
    val filters: ProductFilters = ProductFilters(),
    val preferences: UserPreferences = UserPreferences(),
    val expanded: Boolean = false
)
