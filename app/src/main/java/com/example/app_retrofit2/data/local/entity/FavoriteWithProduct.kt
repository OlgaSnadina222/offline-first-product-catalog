package com.example.app_retrofit2.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FavoriteWithProduct(
    @Embedded
    val favorite: FavoriteEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity
)
