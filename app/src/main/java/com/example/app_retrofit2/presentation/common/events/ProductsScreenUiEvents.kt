package com.example.app_retrofit2.presentation.common.events

import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.model.ThemeMode

sealed class ProductsScreenUiEvents {
    data class OnQueryChange(val query: String): ProductsScreenUiEvents()
    data class OnCategorySelected(val categorySlug: String) : ProductsScreenUiEvents()
    data class ToggleFavorite(val productId: Int) : ProductsScreenUiEvents()
    data class OnThemeSelected(val theme: ThemeMode) : ProductsScreenUiEvents()
    data class OnSortSelected(val sort: ProductSort) : ProductsScreenUiEvents()

}