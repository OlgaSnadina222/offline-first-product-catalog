package com.example.app_retrofit2.data.remote.dto

data class UpdateProductDto(
    val title: String,
    val description: String,
    val price: Float,
    val rating: Float,
    val stock: Int,
    val brand: String
)
