package com.example.app_retrofit2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String?,
    val description: String?,
    val category: String?,
    val price: Float?,
    val discountPercentage: Float,
    val rating: Float,
    val stock: Int,
    val brand: String?,
    val images: List<String>?,
    val isVisible: Boolean = true
)
