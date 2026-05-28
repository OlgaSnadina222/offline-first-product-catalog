package com.example.app_retrofit2.data.remote.repository

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.map
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toDomain
import com.example.app_retrofit2.data.remote.mapper.toDto
import com.example.app_retrofit2.data.remote.mapper.toRequestDto
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val remoteProductDataSource: RemoteProductDataSource
): ProductRepo {
    override suspend fun getAllProducts(limit: Int, skip: Int): Result<List<Product>> {
        return remoteProductDataSource.getAllProducts(limit, skip).map { list ->
            list.map { productDto ->
                productDto.toDomain()
            }
        }
    }

    override suspend fun getProductById(id: Int): Result<Product?> {
        return remoteProductDataSource.getProductById(id).map { it?.toDomain() }
    }

    override fun searchProducts(query: String): Flow<PagingData<Product>> {
        return remoteProductDataSource.searchProducts(query).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createProduct(product: Product): Result<Product> {
        return remoteProductDataSource.createProduct(product.toRequestDto()).map {
            it.toDomain()
        }
    }

    override suspend fun updateProduct(id: Int, product: Product): Result<Product> {
        return remoteProductDataSource.updateProduct(id, product.toRequestDto()).map {
            it.toDomain()
        }
    }

    override suspend fun patchProduct(id: Int, fields: Map<String, Any>): Result<Product> {
        return remoteProductDataSource.patchProduct(id, fields).map {
            it.toDomain()
        }
    }

    override suspend fun deleteProduct(id: Int): Result<Unit> {
        return remoteProductDataSource.deleteProduct(id)
    }

    override fun getPagedProducts(query: String, category: String): Flow<PagingData<Product>> {
        return remoteProductDataSource.getPagedProducts(
            query = query, category = category).map { pagingData ->
            pagingData.map { productDto ->
                productDto.toDomain() }
            }
    }
}



