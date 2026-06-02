package com.example.app_retrofit2.presentation.products_screen

import com.example.app_retrofit2.domain.model.Category

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategorySlug: String = "all"
)
