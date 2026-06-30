package com.example.app_retrofit2.domain.model

data class UserPreferences(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val sort: ProductSort = ProductSort.DEFAULT,
    val selectedCategory: String = "all"
)
