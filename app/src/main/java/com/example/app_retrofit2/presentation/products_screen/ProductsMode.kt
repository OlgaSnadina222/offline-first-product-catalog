package com.example.app_retrofit2.presentation.products_screen

sealed interface ProductsMode {
    data class All(val category: String) : ProductsMode
    data class SearchMode(val query: String) : ProductsMode
}