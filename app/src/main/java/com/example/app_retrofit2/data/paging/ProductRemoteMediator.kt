package com.example.app_retrofit2.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.app_retrofit2.data.local.datasource.room.LocalProductDataSource
import com.example.app_retrofit2.data.local.entity.ProductEntity
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toEntity

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val remote: RemoteProductDataSource,
    private val local: LocalProductDataSource,
    private val query: String,
    private val category: String
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
            val products = when {
                query.isNotBlank() && category != "all" -> {
                    remote.searchProducts(
                        query = query,
                        limit = state.config.pageSize,
                        skip = skip
                    ).getOrThrow()
                        .filter {
                            it.category.equals(
                                category,
                                ignoreCase = true
                            )
                        }
                }
                query.isNotBlank() -> {
                    remote.searchProducts(
                        query = query,
                        limit = state.config.pageSize,
                        skip = skip
                    ).getOrThrow()
                }
                category != "all" -> {
                    remote.getProductsByCategory(
                        category = category,
                        limit = state.config.pageSize,
                        skip = skip
                    ).getOrThrow()
                }
                else -> {
                    remote.getAllProducts(
                        limit = state.config.pageSize,
                        skip = skip
                    ).getOrThrow()
                }
            }

            if (loadType == LoadType.REFRESH) {
                local.clearProducts()
            }

            local.insertProducts(
                products.map { it.toEntity() }
            )

            MediatorResult.Success(
                endOfPaginationReached = products.isEmpty()
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}