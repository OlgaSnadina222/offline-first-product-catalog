package com.example.app_retrofit2.data.local.datasource.room

import androidx.paging.PagingSource
import com.example.app_retrofit2.data.local.dao.FavoriteDao
import com.example.app_retrofit2.data.local.dao.ProductDao
import com.example.app_retrofit2.data.local.entity.FavoriteEntity
import com.example.app_retrofit2.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalProductDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao
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

    suspend fun addFavorite(productId: Int) {
        favoriteDao.addFavorite(FavoriteEntity(productId))
    }

    suspend fun removeFavorite(productId: Int) {
        favoriteDao.removeFavorite(productId)
    }

    suspend fun isFavorite(productId: Int): Boolean {
        return favoriteDao.exists(productId)
    }

    fun getFavoriteIds(): Flow<List<Int>> {
        return favoriteDao.getFavoriteIds()
    }

    fun getFavoriteProducts(): Flow<List<ProductEntity>> {
        return productDao.getFavoriteProducts()
    }

}