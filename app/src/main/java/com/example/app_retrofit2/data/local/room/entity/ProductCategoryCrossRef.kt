package com.example.app_retrofit2.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "product_category_cross_ref",
    primaryKeys = ["productId", "categorySlug"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["slug"],
            childColumns = ["categorySlug"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("productId"),
        Index("categorySlug")
    ]
)
data class ProductCategoryCrossRef(
    val productId: Int,
    val categorySlug: String
)
