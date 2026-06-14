package com.example.app_retrofit2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app_retrofit2.data.local.entity.CacheInfoEntity

@Dao
interface CacheInfoDao {
    @Query("SELECT * FROM CacheInfoEntity WHERE `key` = :key")
    suspend fun get(key: String): CacheInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cacheInfo: CacheInfoEntity)
}