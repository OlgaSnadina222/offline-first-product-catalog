package com.example.app_retrofit2.data.remote.datasource.dummyjson

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.app_retrofit2.data.remote.api.ProductApi
import com.example.app_retrofit2.data.remote.dto.ProductDto
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

    suspend fun getProductById(id: Int): Result<ProductDto> {
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

    suspend fun searchProducts(query: String, limit: Int, skip: Int): Result<List<ProductDto>> {
        return try {
            val response = productApi.searchProducts(
                query = query,
                limit = limit,
                skip = skip
            )
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

    suspend fun getProductsByCategory(category: String, limit: Int, skip: Int): Result<List<ProductDto>> {
        return try {
            val response = productApi.getProductsByCategory(
                category = category,
                limit = limit,
                skip = skip
            )
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

//    suspend fun createProduct(dto: ProductRequestDto): Result<ProductDto> =
//        handle { productApi.createProduct(dto) }
//
//    suspend fun updateProduct(id: Int, dto: ProductRequestDto): Result<ProductDto> =
//        handle { productApi.updateProduct(id, dto) }
//
//    suspend fun patchProduct(id: Int, fields: Map<String, Any>): Result<ProductDto> =
//        handle { productApi.patchProduct(id, fields) }
//
//    suspend fun deleteProduct(id: Int): Result<Unit> =
//        handle { productApi.deleteProduct(id) }

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
}