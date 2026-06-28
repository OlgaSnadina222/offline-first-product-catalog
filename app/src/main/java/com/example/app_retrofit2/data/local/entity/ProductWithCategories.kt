package com.example.app_retrofit2.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ProductWithCategories(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "id",
        entity = CategoryEntity::class,
        entityColumn = "slug",
        associateBy = Junction(
            ProductCategoryCrossRefEntity::class,
            parentColumn = "productId",
            entityColumn = "categorySlug"
        )
    )
    val categories: List<CategoryEntity>
)
