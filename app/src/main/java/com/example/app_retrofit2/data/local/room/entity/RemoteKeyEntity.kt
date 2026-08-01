package com.example.app_retrofit2.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(

    @PrimaryKey val id: String,
    val nextKey: Int?
)
