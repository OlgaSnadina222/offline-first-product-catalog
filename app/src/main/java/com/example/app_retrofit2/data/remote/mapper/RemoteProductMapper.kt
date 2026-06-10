package com.example.app_retrofit2.data.remote.mapper

import com.example.app_retrofit2.data.local.entity.ProductEntity
import com.example.app_retrofit2.data.remote.dto.ProductDto

fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
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

fun ProductEntity.toDto(): ProductDto {
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
