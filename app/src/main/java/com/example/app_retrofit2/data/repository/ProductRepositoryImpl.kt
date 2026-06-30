package com.example.app_retrofit2.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.app_retrofit2.data.local.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.datasource.room.LocalProductDataSource
import com.example.app_retrofit2.data.local.db.AppDatabase
import com.example.app_retrofit2.data.local.mapper.toDomain
import com.example.app_retrofit2.data.local.mapper.toEntity
import com.example.app_retrofit2.data.paging.ProductRemoteMediator
import com.example.app_retrofit2.data.paging.SearchProductsPagingSource
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toEntity
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteProductDataSource: RemoteProductDataSource,
    private val localProductDataSource: LocalProductDataSource,
    private val cacheInfoDao: CacheInfoDao,
    private val database: AppDatabase
) : ProductRepo {

    override suspend fun insertProducts(products: List<Product>) {
        localProductDataSource.insertProducts(products.map { it.toEntity() })
    }

    override suspend fun clearProducts(category: String) {
        localProductDataSource.clearProducts(category)
    }

    override fun searchProducts(query: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                SearchProductsPagingSource(
                    remoteRepo = remoteProductDataSource,
                    query = query
                )
            }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedProducts(category: String, sort: ProductSort): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = ProductRemoteMediator(
                remote = remoteProductDataSource,
                local = localProductDataSource,
                category = category,
                cacheInfoDao = cacheInfoDao,
                database = database
            ),
            pagingSourceFactory = {
                localProductDataSource.pagingSource(
                    category = category,
                    sort = sort
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { productWithFavorite ->
                productWithFavorite.product.toDomain()
                    .copy(isFavorite = productWithFavorite.favorite != null)
            }
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        val localProduct = localProductDataSource.getProductById(id)
        if (localProduct != null) {
            return Result.success(localProduct.toDomain())
        }
        return remoteProductDataSource.getProductById(id).map { dto ->
            localProductDataSource.insertProducts(listOf(dto.toEntity()))
            dto.toEntity().toDomain()
        }
    }

    override suspend fun toggleFavorite(productId: Int) {
        if (localProductDataSource.isFavorite(productId)) {
            localProductDataSource.removeFavorite(productId)
        } else {
            localProductDataSource.addFavorite(productId)
        }
    }

    override fun getFavoriteProducts(): Flow<List<Product>> {
        return localProductDataSource.getFavoriteProducts().map { favsWithProducts ->
            favsWithProducts.map { favWithProduct ->
                favWithProduct.product.toDomain().copy(isFavorite = true) }
        }
    }
}

