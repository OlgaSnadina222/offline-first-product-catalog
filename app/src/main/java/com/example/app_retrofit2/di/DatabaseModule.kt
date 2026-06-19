package com.example.app_retrofit2.di

import android.content.Context
import androidx.room.Room
import com.example.app_retrofit2.data.local.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.dao.FavoriteDao
import com.example.app_retrofit2.data.local.dao.ProductDao
import com.example.app_retrofit2.data.local.db.AppDatabase
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

}