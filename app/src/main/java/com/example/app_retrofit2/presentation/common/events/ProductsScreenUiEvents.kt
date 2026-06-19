package com.example.app_retrofit2.presentation.common.events

import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.model.Product

sealed class ProductsScreenUiEvents {
    data class OnQueryChange(val query: String): ProductsScreenUiEvents()
    data class OnCategorySelected(val categorySlug: String) : ProductsScreenUiEvents()
    data class ToggleFavorite(val productId: Int) : ProductsScreenUiEvents()
}