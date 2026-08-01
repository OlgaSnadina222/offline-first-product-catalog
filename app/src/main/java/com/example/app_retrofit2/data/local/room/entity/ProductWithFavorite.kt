package com.example.app_retrofit2.data.local.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithFavorite(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val favorite: FavoriteEntity?

)
