package com.example.app_retrofit2.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.app_retrofit2.data.local.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.dao.FavoriteDao
import com.example.app_retrofit2.data.local.dao.ProductDao
import com.example.app_retrofit2.data.local.entity.CacheInfoEntity
import com.example.app_retrofit2.data.local.entity.FavoriteEntity
import com.example.app_retrofit2.data.local.entity.ProductEntity
import com.example.app_retrofit2.data.local.utils.Converters

@Database(
    version = 1,
    entities = [
        ProductEntity::class,
        CacheInfoEntity::class,
        FavoriteEntity::class
               ],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cacheInfoDao(): CacheInfoDao
    abstract fun favoriteDao(): FavoriteDao
}