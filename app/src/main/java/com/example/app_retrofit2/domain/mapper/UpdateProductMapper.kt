package com.example.app_retrofit2.domain.mapper

import com.example.app_retrofit2.data.remote.dto.UpdateProductDto
import com.example.app_retrofit2.domain.model.Product

fun Product.toUpdateDto(): UpdateProductDto {
    return UpdateProductDto(
        title = title,
        description = description,
        price = price,
        rating = rating,
        stock = stock,
        brand = brand
    )
}