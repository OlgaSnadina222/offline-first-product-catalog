package com.example.app_retrofit2.data.local.datasource.room

import androidx.paging.PagingSource
import com.example.app_retrofit2.data.local.dao.CategoryDao
import com.example.app_retrofit2.data.local.dao.FavoriteDao
import com.example.app_retrofit2.data.local.dao.ProductDao
import com.example.app_retrofit2.data.local.dao.RemoteKeyDao
import com.example.app_retrofit2.data.local.entity.CategoryEntity
import com.example.app_retrofit2.data.local.entity.FavoriteEntity
import com.example.app_retrofit2.data.local.entity.FavoriteWithProduct
import com.example.app_retrofit2.data.local.entity.ProductEntity
import com.example.app_retrofit2.data.local.entity.ProductWithFavorite
import com.example.app_retrofit2.data.local.entity.RemoteKeyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalProductDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val categoryDao: CategoryDao
){
    fun getProducts(): Flow<List<ProductEntity>> {
        return productDao.getProducts()
    }
    fun pagingSource(category: String): PagingSource<Int, ProductWithFavorite> {
        return productDao.pagingSource(category)
    }

    suspend fun getProductById(id: Int): ProductEntity? {
        return productDao.getProductById(id)
    }

    suspend fun clearProducts(category: String) {
        productDao.clearProducts(category)
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

    fun getFavoriteProducts(): Flow<List<FavoriteWithProduct>> {
        return favoriteDao.getFavoriteProducts()
    }

    suspend fun getRemoteKey(category: String): RemoteKeyEntity? {
        return remoteKeyDao.getRemoteKey(category)
    }

    suspend fun insertRemoteKey(nextKey: Int?, category: String) {
        remoteKeyDao.insertRemoteKey(
            RemoteKeyEntity(
                id = category,
                nextKey = nextKey
            )
        )
    }

    suspend fun clearRemoteKeys(category: String) {
        remoteKeyDao.clearRemoteKeys(category)
    }

    suspend fun getCategories(): List<CategoryEntity> {
        return categoryDao.getCategories()
    }

    fun observeCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.observeCategories()
    }

    suspend fun insertCategories(categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
    }

    suspend fun clearCategories() {
        categoryDao.clearCategories()
    }
}