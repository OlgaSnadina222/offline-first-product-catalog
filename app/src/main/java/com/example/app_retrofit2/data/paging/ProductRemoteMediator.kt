package com.example.app_retrofit2.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.app_retrofit2.data.local.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.datasource.room.LocalProductDataSource
import com.example.app_retrofit2.data.local.entity.CacheInfoEntity
import com.example.app_retrofit2.data.local.entity.ProductEntity
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toEntity
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val remote: RemoteProductDataSource,
    private val local: LocalProductDataSource,
    private val category: String,
    private val cacheInfoDao: CacheInfoDao,
) : RemoteMediator<Int, ProductEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val skip = when(loadType) {
                LoadType.REFRESH -> 0
                LoadType.APPEND -> local.getProductsCount()
                LoadType.PREPEND -> return MediatorResult.Success(true)
            }
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

            if (loadType == LoadType.REFRESH) {
                local.clearProducts()
            }
            local.insertProducts(products.map { it.toEntity() })
            cacheInfoDao.insert(CacheInfoEntity(
                key = "products",
                lastUpdated = System.currentTimeMillis())
            )
            MediatorResult.Success(
                endOfPaginationReached = products.size < state.config.pageSize
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
}