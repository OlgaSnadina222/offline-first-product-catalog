package com.example.app_retrofit2.data.local.datasource.room

import androidx.paging.PagingSource
import com.example.app_retrofit2.data.local.dao.ProductDao
import com.example.app_retrofit2.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalProductDataSource @Inject constructor(
    private val productDao: ProductDao
){
    fun getProducts(): Flow<List<ProductEntity>> {
        return productDao.getProducts()
    }
    fun pagingSource(): PagingSource<Int, ProductEntity> {
        return productDao.pagingSource()
    }

    suspend fun getProductById(id: Int): ProductEntity? {
        return productDao.getProductById(id)
    }

    suspend fun clearProducts() {
        productDao.clearProducts()
    }

    suspend fun insertProducts(products: List<ProductEntity>) {
        productDao.insertProducts(products)
    }

    suspend fun getProductsCount(): Int {
        return productDao.getProductsCount()
    }
}