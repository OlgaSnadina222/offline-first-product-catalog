package com.example.app_retrofit2.data.remote.mapper

import com.example.app_retrofit2.data.remote.dto.ProductDto
import com.example.app_retrofit2.data.remote.dto.ProductRequestDto
import com.example.app_retrofit2.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title ?: "",
        description = description ?: "",
        category = category ?: "unknown",
        price = price ?: 0f,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand ?: "unbranded",
        images = images ?: emptyList()
    )
}

fun Product.toDto(): ProductDto {
    return ProductDto(
        id = id,
        title = title,
        description = description,
        category = category,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        images = images
    )
}

fun Product.toRequestDto(): ProductRequestDto {
    return ProductRequestDto(
        title = title,
        price = price,
        description = description,
        brand = brand,
        category = category
    )
}
