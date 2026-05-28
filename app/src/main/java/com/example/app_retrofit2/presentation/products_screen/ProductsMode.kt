package com.example.app_retrofit2.presentation.products_screen

sealed class ProductsMode {
    object All: ProductsMode()
    data class SearchMode(val query: String): ProductsMode()
}