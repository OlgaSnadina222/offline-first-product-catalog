package com.example.app_retrofit2.data.repository

import android.provider.ContactsContract
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.app_retrofit2.data.local.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.datasource.room.LocalProductDataSource
import com.example.app_retrofit2.data.local.entity.ProductEntity
import com.example.app_retrofit2.data.local.mapper.toDomain
import com.example.app_retrofit2.data.local.mapper.toEntity
import com.example.app_retrofit2.data.paging.ProductRemoteMediator
import com.example.app_retrofit2.data.paging.SearchProductsPagingSource
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toDomain
import com.example.app_retrofit2.data.remote.mapper.toEntity
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteRepo: RemoteProductDataSource,
    private val localRepo: LocalProductDataSource,
    private val cacheInfoDao: CacheInfoDao
) : ProductRepo {

    override suspend fun insertProducts(products: List<Product>) {
        localRepo.insertProducts(products.map { it.toEntity() })
    }

    override suspend fun clearProducts() {
        localRepo.clearProducts()
    }

    override fun searchProducts(query: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                SearchProductsPagingSource(
                    remoteRepo = remoteRepo,
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
                remote = remoteRepo,
                local = localRepo,
                category = category,
                cacheInfoDao = cacheInfoDao
            ),
            pagingSourceFactory = {
                localRepo.pagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map {
                it.toDomain()
            }
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        val localProduct = localRepo.getProductById(id)
        if (localProduct != null) {
            return Result.success(localProduct.toDomain())
        }
        return remoteRepo.getProductById(id).map { dto ->
            localRepo.insertProducts(listOf(dto.toEntity()))
                dto.toEntity().toDomain()
        }
    }
}

