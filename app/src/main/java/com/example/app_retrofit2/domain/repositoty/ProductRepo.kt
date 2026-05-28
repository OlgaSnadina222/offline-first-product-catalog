package com.example.app_retrofit2.domain.repositoty

import androidx.paging.PagingData
import com.example.app_retrofit2.domain.model.Product
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ProductRepo {
    suspend fun getAllProducts(limit: Int, skip: Int): Result<List<Product>>
    suspend fun getProductById(id: Int): Result<Product?>
    fun searchProducts(query: String): Flow<PagingData<Product>>
    suspend fun createProduct(product: Product): Result<Product>
    suspend fun updateProduct(id: Int, product: Product): Result<Product>
    suspend fun patchProduct(id: Int, fields: Map<String, Any>): Result<Product>
    suspend fun deleteProduct(id: Int): Result<Unit>
    fun getPagedProducts(query: String, category: String): Flow<PagingData<Product>>
}