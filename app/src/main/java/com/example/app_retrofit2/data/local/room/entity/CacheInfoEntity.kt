package com.example.app_retrofit2.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CacheInfoEntity(
    @PrimaryKey
    val key: String,
    val lastUpdated: Long
)