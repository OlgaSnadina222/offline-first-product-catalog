package com.example.app_retrofit2.domain.repositoty

import androidx.paging.PagingData
import com.example.app_retrofit2.data.local.entity.ProductEntity
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.ProductSort
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ProductRepo {
    suspend fun insertProducts(products: List<Product>)
    suspend fun clearProducts(category: String)
    fun getPagedProducts(category: String, sort: ProductSort): Flow<PagingData<Product>>
    fun searchProducts(query: String): Flow<PagingData<Product>>
    suspend fun getProductById(id: Int): Result<Product>
    suspend fun toggleFavorite(productId: Int)
    fun getFavoriteProducts(): Flow<List<Product>>
}

