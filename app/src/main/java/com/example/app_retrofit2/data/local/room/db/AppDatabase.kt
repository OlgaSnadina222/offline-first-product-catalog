package com.example.app_retrofit2.data.local.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.app_retrofit2.data.local.room.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.room.dao.CategoryDao
import com.example.app_retrofit2.data.local.room.dao.FavoriteDao
import com.example.app_retrofit2.data.local.room.dao.PendingOperationDao
import com.example.app_retrofit2.data.local.room.dao.ProductCategoryCrossRefDao
import com.example.app_retrofit2.data.local.room.dao.ProductDao
import com.example.app_retrofit2.data.local.room.dao.RemoteKeyDao
import com.example.app_retrofit2.data.local.room.entity.CacheInfoEntity
import com.example.app_retrofit2.data.local.room.entity.CategoryEntity
import com.example.app_retrofit2.data.local.room.entity.FavoriteEntity
import com.example.app_retrofit2.data.local.room.entity.PendingOperationEntity
import com.example.app_retrofit2.data.local.room.entity.ProductCategoryCrossRef
import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.local.room.entity.RemoteKeyEntity
import com.example.app_retrofit2.data.local.room.utils.Converters

@Database(
    version = 1,
    entities = [
        ProductEntity::class,
        CacheInfoEntity::class,
        FavoriteEntity::class,
        RemoteKeyEntity::class,
        CategoryEntity::class,
        ProductCategoryCrossRef::class,
        PendingOperationEntity::class
               ],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cacheInfoDao(): CacheInfoDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productCategoryCrossRefDao(): ProductCategoryCrossRefDao
    abstract fun pendingOperationDao(): PendingOperationDao
}