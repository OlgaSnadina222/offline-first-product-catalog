package com.example.app_retrofit2.di

import android.content.Context
import androidx.room.Room
import com.example.app_retrofit2.data.local.room.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.room.dao.CategoryDao
import com.example.app_retrofit2.data.local.room.dao.FavoriteDao
import com.example.app_retrofit2.data.local.room.dao.PendingOperationDao
import com.example.app_retrofit2.data.local.room.dao.ProductCategoryCrossRefDao
import com.example.app_retrofit2.data.local.room.dao.ProductDao
import com.example.app_retrofit2.data.local.room.dao.RemoteKeyDao
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db")
            .build()
    }
    @Provides
    fun provideProductDao(db: AppDatabase) : ProductDao = db.productDao()

    @Provides
    fun provideCacheInfoDao(db: AppDatabase) : CacheInfoDao = db.cacheInfoDao()

    @Provides
    fun provideFavoriteDao(db: AppDatabase) : FavoriteDao = db.favoriteDao()

    @Provides
    fun provideRemoteKeyDao(db: AppDatabase) : RemoteKeyDao = db.remoteKeyDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase) : CategoryDao = db.categoryDao()

    @Provides
    fun provideProductCategoryCrossRefDao(db: AppDatabase) : ProductCategoryCrossRefDao = db.productCategoryCrossRefDao()

    @Provides
    fun providePendingOperationDao(db: AppDatabase) : PendingOperationDao = db.pendingOperationDao()

}