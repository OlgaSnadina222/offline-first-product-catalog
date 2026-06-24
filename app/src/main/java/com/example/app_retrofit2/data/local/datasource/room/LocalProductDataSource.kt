package com.example.app_retrofit2.data.local.datasource.room

import androidx.paging.PagingSource
import com.example.app_retrofit2.data.local.dao.FavoriteDao
import com.example.app_retrofit2.data.local.dao.ProductDao
import com.example.app_retrofit2.data.local.dao.RemoteKeyDao
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
    private val remoteKeyDao: RemoteKeyDao
){
    fun getProducts(): Flow<List<ProductEntity>> {
        return productDao.getProducts()
    }
    fun pagingSource(): PagingSource<Int, ProductWithFavorite> {
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

    fun getFavoriteProducts(): Flow<List<FavoriteWithProduct>> {
        return favoriteDao.getFavoriteProducts()
    }

    suspend fun getRemoteKey(): RemoteKeyEntity? {
        return remoteKeyDao.getRemoteKey("products")
    }

    suspend fun insertRemoteKey(nextKey: Int?) {
        remoteKeyDao.insertRemoteKey(
            RemoteKeyEntity(
                id = "products",
                nextKey = nextKey
            )
        )
    }

    suspend fun clearRemoteKeys() {
        remoteKeyDao.clearRemoteKeys()
    }
}