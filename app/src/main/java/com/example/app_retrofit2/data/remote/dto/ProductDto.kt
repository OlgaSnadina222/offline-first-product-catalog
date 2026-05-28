package com.example.app_retrofit2.data.remote.dto

data class ProductDto(
    val id: Int,
    val title: String?,
    val description: String?,
    val category: String?,
    val price: Float?,
    val discountPercentage: Float,
    val rating: Float,
    val stock: Int,
    val brand: String?,
    val images: List<String>?
)
