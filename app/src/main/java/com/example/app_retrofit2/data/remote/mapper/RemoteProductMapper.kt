package com.example.app_retrofit2.data.remote.mapper

import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.remote.dto.ProductDto
import com.example.app_retrofit2.data.sync.SyncStatus

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
        images = images ?: emptyList(),
        isVisible = true,
        isDeleted = false,
        syncStatus = SyncStatus.SYNCED,
        updatedAt = System.currentTimeMillis()
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

