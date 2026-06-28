package com.example.app_retrofit2.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.app_retrofit2.data.local.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.datasource.room.LocalProductDataSource
import com.example.app_retrofit2.data.local.db.AppDatabase
import com.example.app_retrofit2.data.local.entity.CacheInfoEntity
import com.example.app_retrofit2.data.local.entity.ProductCategoryCrossRefEntity
import com.example.app_retrofit2.data.local.entity.ProductWithFavorite
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toEntity

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val remote: RemoteProductDataSource,
    private val local: LocalProductDataSource,
    private val category: String,
    private val cacheInfoDao: CacheInfoDao,
    private val database: AppDatabase
) : RemoteMediator<Int, ProductWithFavorite>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductWithFavorite>
    ): MediatorResult {
        return try {
            val skip = when(loadType) {
                LoadType.REFRESH -> 0
                LoadType.APPEND -> {
                    val remoteKey = local.getRemoteKey(category)
                    val nextKey = remoteKey?.nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextKey
                }
                LoadType.PREPEND -> return MediatorResult.Success(true)
            }
            Log.d("MediatorLog", "Api call")
            val products = if (category != "all") {
                remote.getProductsByCategory(
                        category = category,
                        limit = state.config.pageSize,
                        skip = skip
                    ).getOrThrow()
                }
                else {
                    remote.getAllProducts(
                        limit = state.config.pageSize,
                        skip = skip
                    ).getOrThrow()
                }
            val productEntities = products.map { it.toEntity() }
            val crossRefs = products.map {
                    ProductCategoryCrossRefEntity(
                        productId = it.id,
                        categorySlug = it.category ?: ""
                    )
                }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    local.clearProducts(category)
                    local.clearCrossRefs()
                    local.clearRemoteKeys(category)
                }
                local.insertProducts(productEntities)
                local.insertCrossRefs(crossRefs)
                local.insertRemoteKey(
                    category = category,
                    nextKey = if (products.size < state.config.pageSize) {
                        null
                    } else {
                        skip + products.size
                    }
                )
                cacheInfoDao.insertCacheInfo(CacheInfoEntity(
                    key = "products_$category",
                    lastUpdated = System.currentTimeMillis())
                )
            }
            MediatorResult.Success(
                endOfPaginationReached = products.size < state.config.pageSize
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        val cacheInfo = cacheInfoDao.getCacheInfo("products_$category")
        return if (
            cacheInfo == null ||
            System.currentTimeMillis() - cacheInfo.lastUpdated > 20 * 1000
        ) {
            Log.d("MediatorLog", "LAUNCH_INITIAL_REFRESH")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            Log.d("MediatorLog", "SKIP_INITIAL_REFRESH ")
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }
}