package com.example.app_retrofit2.presentation.common.events

import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.presentation.products_screen.ProductCategory

sealed class ProductsScreenUiEvents {
    data class OnQueryChange(val query: String): ProductsScreenUiEvents()
    data class CreateProduct(val product: Product): ProductsScreenUiEvents()
    data class UpdateProduct(val id: Int, val product: Product): ProductsScreenUiEvents()
    data class DeleteProduct(val id: Int): ProductsScreenUiEvents()
    data class OnCategorySelected(val category: ProductCategory) : ProductsScreenUiEvents()
}