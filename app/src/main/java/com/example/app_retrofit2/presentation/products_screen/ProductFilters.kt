package com.example.app_retrofit2.presentation.products_screen

data class ProductFilters(
    val query: String = "",
    val category: String = "all",
    val mode: ProductsMode = ProductsMode.CATEGORY
)
