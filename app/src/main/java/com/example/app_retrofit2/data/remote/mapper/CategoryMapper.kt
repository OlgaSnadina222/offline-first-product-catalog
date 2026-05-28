package com.example.app_retrofit2.data.remote.mapper

import com.example.app_retrofit2.data.remote.dto.CategoryDto
import com.example.app_retrofit2.domain.model.Category

fun CategoryDto.toDomain(): Category {
    return Category(
        slug = slug ?: "unknown",
        name = name ?: "unknown",
        url = url ?: ""
    )
}