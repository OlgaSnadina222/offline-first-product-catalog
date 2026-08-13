package com.example.app_retrofit2.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.app_retrofit2.data.local.room.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.room.datasource.LocalProductDataSource
import com.example.app_retrofit2.data.local.room.entity.CacheInfoEntity
import com.example.app_retrofit2.data.local.room.entity.ProductCategoryCrossRef
import com.example.app_retrofit2.data.local.room.entity.ProductWithFavorite
import com.example.app_retrofit2.data.local.room.transaction.DatabaseTransaction
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toEntity
import com.example.app_retrofit2.data.sync.SyncStatus

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val remote: RemoteProductDataSource,
    private val local: LocalProductDataSource,
    private val category: String,
    private val cacheInfoDao: CacheInfoDao,
    private val transaction: DatabaseTransaction
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
            //Log.d("MediatorLog", "Api call: ${loadType.name}")
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
                    ProductCategoryCrossRef(
                        productId = it.id,
                        categorySlug = it.category ?: ""
                    )
                }
            transaction.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    local.clearCrossRefs(category)
                    local.clearRemoteKeys(category)
                }
                productEntities.forEach { serverProduct ->
                    val localProduct = local.getProductById(serverProduct.id)
                    when {
                        localProduct == null -> {
                            local.insertProducts(listOf(serverProduct))
                        }
                        localProduct.isDeleted -> Unit
                        localProduct.syncStatus == SyncStatus.PENDING -> Unit
                        else -> {
                            local.updateProduct(serverProduct)
                        }
                    }
                }
                local.insertCrossRefs(crossRefs)
                local.insertRemoteKey(
                    category = category,
                    nextKey = if (products.size < state.config.pageSize) {
                        null
                    } else {
                        skip + products.size
                    }
                )
                cacheInfoDao.insertCacheInfo(
                    CacheInfoEntity(
                        key = "products_$category",
                        lastUpdated = System.currentTimeMillis()
                    )
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
        return if (local.isCacheExpired(category)) {
            //Log.d("MediatorLog", "Cache expired")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            //Log.d("MediatorLog", "Cache is fresh")
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }
}