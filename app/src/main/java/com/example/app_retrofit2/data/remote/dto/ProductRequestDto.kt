package com.example.app_retrofit2.data.remote.dto

data class ProductRequestDto (
    val title: String,
    val category: String,
    val price: Float,
    val brand: String,
    val description: String
)