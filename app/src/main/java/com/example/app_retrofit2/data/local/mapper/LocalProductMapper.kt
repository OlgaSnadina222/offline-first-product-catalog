package com.example.app_retrofit2.data.local.mapper

import com.example.app_retrofit2.data.local.entity.ProductEntity
import com.example.app_retrofit2.domain.model.Product

fun ProductEntity.toDomain(): Product{
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

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
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
