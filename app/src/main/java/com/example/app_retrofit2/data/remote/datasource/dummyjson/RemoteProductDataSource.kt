package com.example.app_retrofit2.data.remote.datasource.dummyjson

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.app_retrofit2.data.remote.api.ProductApi
import com.example.app_retrofit2.data.remote.dto.ProductDto
import com.example.app_retrofit2.data.remote.dto.ProductRequestDto
import com.example.app_retrofit2.data.remote.mapper.toDomain
import com.example.app_retrofit2.data.remote.paging.ProductsPagingSource
import com.example.app_retrofit2.data.remote.paging.SearchProductsPagingSource
import com.example.app_retrofit2.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class RemoteProductDataSource @Inject constructor(
    private val productApi: ProductApi
) {
    suspend fun getAllProducts(limit: Int, skip: Int): Result<List<ProductDto>> {
        return try {
            val response = productApi.getAllProducts(limit, skip)
            if (response.isSuccessful) {
                val body = response.body() ?: return Result.failure(Exception("Body is null"))
                Result.success(body.products)
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()}"))
            }
        } catch (ioException: IOException) {
            Result.failure(ioException)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Int): Result<ProductDto?> {
        return try {
            val response = productApi.getProductById(id)
            if (response.isSuccessful) {
                val body = response.body() ?: return Result.failure(Exception("Body is null"))
                Result.success(body)
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()}"))
            }
        } catch (ioException: IOException) {
            Result.failure(ioException)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun searchProducts(query: String): Flow<PagingData<ProductDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchProductsPagingSource(productApi, query)
            }
        ).flow
    }

    suspend fun createProduct(dto: ProductRequestDto): Result<ProductDto> =
        handle { productApi.createProduct(dto) }

    suspend fun updateProduct(id: Int, dto: ProductRequestDto): Result<ProductDto> =
        handle { productApi.updateProduct(id, dto) }

    suspend fun patchProduct(id: Int, fields: Map<String, Any>): Result<ProductDto> =
        handle { productApi.patchProduct(id, fields) }

    suspend fun deleteProduct(id: Int): Result<Unit> =
        handle { productApi.deleteProduct(id) }

    private suspend fun <T> handle (call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (ioException: IOException) {
            Result.failure(ioException)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPagedProducts(query: String, category: String): Flow<PagingData<ProductDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProductsPagingSource(
                    productApi = productApi,
                    query = query,
                    category = category
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it }
        }
    }
}