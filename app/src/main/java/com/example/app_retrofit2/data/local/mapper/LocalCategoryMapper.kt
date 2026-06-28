package com.example.app_retrofit2.data.local.mapper

import com.example.app_retrofit2.data.local.entity.CategoryEntity
import com.example.app_retrofit2.domain.model.Category

fun CategoryEntity.toDomain(): Category {
    return Category(
        slug = slug,
        name = name
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        slug = slug,
        name = name
    )
}