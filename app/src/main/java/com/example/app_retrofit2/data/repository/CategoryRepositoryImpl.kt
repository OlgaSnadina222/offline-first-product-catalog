package com.example.app_retrofit2.data.repository

import androidx.room.withTransaction
import com.example.app_retrofit2.data.local.room.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.room.datasource.LocalCategoryDataSource
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import com.example.app_retrofit2.data.local.room.entity.CacheInfoEntity
import com.example.app_retrofit2.data.local.room.mapper.toDomain
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteCategoryDataSource
import com.example.app_retrofit2.data.remote.mapper.toDomain
import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.repositoty.CategoryRepo
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val localCategoryDataSource: LocalCategoryDataSource,
    private val remoteCategoryDataSource: RemoteCategoryDataSource,
    private val cacheInfoDao: CacheInfoDao,
    private val database: AppDatabase
) : CategoryRepo {
    override suspend fun getCategories(forceRefresh: Boolean): Result<List<Category>> {
        val cacheInfo = cacheInfoDao.getCacheInfo("categories")
        val cacheExpired = cacheInfo == null ||
                System.currentTimeMillis() - cacheInfo.lastUpdated > 24 * 60 * 60 * 1000L
        if (!forceRefresh && !cacheExpired) {
            val cached = localCategoryDataSource.getCategories()
            if (cached.isNotEmpty()) {
                return Result.success(
                    cached.map { it.toDomain() }
                )
            }
        }
        return remoteCategoryDataSource.getCategories().map { dtoList ->
            val categories = dtoList.map { it.toDomain() }
            database.withTransaction {
                localCategoryDataSource.clearCategories()
                localCategoryDataSource.insertCategories(
                    categories
                )
                cacheInfoDao.insertCacheInfo(
                    CacheInfoEntity(
                        key = "categories",
                        lastUpdated = System.currentTimeMillis()
                    )
                )
            }
            categories
            }.recoverCatching {
                localCategoryDataSource.getCategories().map { it.toDomain() }
            }
    }
}