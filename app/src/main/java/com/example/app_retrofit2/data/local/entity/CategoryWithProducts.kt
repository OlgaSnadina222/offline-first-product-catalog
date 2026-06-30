package com.example.app_retrofit2.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CategoryWithProducts(
    @Embedded
    val category: CategoryEntity,

    @Relation(
        parentColumn = "slug",
        entity = ProductEntity::class,
        entityColumn = "id",
        associateBy = Junction(
            ProductCategoryCrossRef::class,
            parentColumn = "categorySlug",
            entityColumn = "productId"
        )
    )
    val products: List<ProductEntity>
)
