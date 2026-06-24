package com.example.app_retrofit2.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.app_retrofit2.data.local.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.datasource.room.LocalProductDataSource
import com.example.app_retrofit2.data.local.mapper.toDomain
import com.example.app_retrofit2.data.local.mapper.toEntity
import com.example.app_retrofit2.data.paging.ProductRemoteMediator
import com.example.app_retrofit2.data.paging.SearchProductsPagingSource
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toEntity
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteProductDataSource,
    private val localDataSource: LocalProductDataSource,
    private val cacheInfoDao: CacheInfoDao
) : ProductRepo {

    override suspend fun insertProducts(products: List<Product>) {
        localDataSource.insertProducts(products.map { it.toEntity() })
    }

    override suspend fun clearProducts() {
        localDataSource.clearProducts()
    }

    override fun searchProducts(query: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                SearchProductsPagingSource(
                    remoteRepo = remoteDataSource,
                    query = query
                )
            }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedProducts(category: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = ProductRemoteMediator(
                remote = remoteDataSource,
                local = localDataSource,
                category = category,
                cacheInfoDao = cacheInfoDao
            ),
            pagingSourceFactory = {
                localDataSource.pagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map { productWithFavorite ->
                productWithFavorite.product.toDomain()
                    .copy(isFavorite = productWithFavorite.favorite != null)
            }
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        val localProduct = localDataSource.getProductById(id)
        if (localProduct != null) {
            return Result.success(localProduct.toDomain())
        }
        return remoteDataSource.getProductById(id).map { dto ->
            localDataSource.insertProducts(listOf(dto.toEntity()))
            dto.toEntity().toDomain()
        }
    }

    override suspend fun toggleFavorite(productId: Int) {
        if (localDataSource.isFavorite(productId)) {
            localDataSource.removeFavorite(productId)
        } else {
            localDataSource.addFavorite(productId)
        }
    }

    override fun getFavoriteProducts(): Flow<List<Product>> {
        return localDataSource.getFavoriteProducts().map { favsWithProducts ->
            favsWithProducts.map { favWithProduct ->
                favWithProduct.product.toDomain().copy(isFavorite = true) }
        }
    }
}

