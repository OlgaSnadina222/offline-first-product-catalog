package com.example.app_retrofit2.data.local.room.datasource

import androidx.paging.PagingSource
import com.example.app_retrofit2.data.cache.CachePolicy
import com.example.app_retrofit2.data.local.room.dao.CategoryDao
import com.example.app_retrofit2.data.local.room.dao.FavoriteDao
import com.example.app_retrofit2.data.local.room.dao.ProductCategoryCrossRefDao
import com.example.app_retrofit2.data.local.room.dao.ProductDao
import com.example.app_retrofit2.data.local.room.dao.RemoteKeyDao
import com.example.app_retrofit2.data.local.room.entity.FavoriteEntity
import com.example.app_retrofit2.data.local.room.entity.FavoriteWithProduct
import com.example.app_retrofit2.data.local.room.entity.ProductCategoryCrossRef
import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.local.room.entity.ProductWithFavorite
import com.example.app_retrofit2.data.local.room.entity.RemoteKeyEntity
import com.example.app_retrofit2.data.local.room.mapper.toDomain
import com.example.app_retrofit2.data.local.room.mapper.toEntity
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.ProductSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalProductDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val categoryDao: CategoryDao,
    private val crossRefDao: ProductCategoryCrossRefDao
){
    fun getProducts(): Flow<List<ProductEntity>> {
        return productDao.getProducts()
    }
    fun pagingSource(category: String, sort: ProductSort): PagingSource<Int, ProductWithFavorite> {
        return when (sort) {
            ProductSort.DEFAULT -> productDao.pagingSource(category)
            ProductSort.PRICE_ASC -> productDao.pagingSourcePriceAsc(category)
            ProductSort.PRICE_DESC -> productDao.pagingSourcePriceDesc(category)
        }
    }

    suspend fun getProductById(id: Int): ProductEntity? {
        return productDao.getProductById(id)?.product
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

    suspend fun insertCrossRefs(crossRefs: List<ProductCategoryCrossRef>) {
        crossRefDao.insertCrossRefs(crossRefs)
    }

    suspend fun clearCrossRefs(category: String) {
        crossRefDao.clearCrossRefs(category)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    fun observeProductById(id: Int): Flow<Product> {
        return productDao.observeProductById(id)
            .filterNotNull().map { productWithFavorite ->
            productWithFavorite.product.toDomain().copy(
                isFavorite = productWithFavorite.favorite != null
            )
        }
    }

    suspend fun softDeleteProduct(productId: Int) {
        productDao.softDelete(productId)
    }

    suspend fun hardDeleteProduct(productId: Int) {
        productDao.hardDelete(productId)
    }

    suspend fun isCacheExpired(category: String, timeout: Long = CachePolicy.DEFAULT_TIMEOUT): Boolean {
        val oldestUpdatedAt = productDao.getOldestUpdatedAt(category)
        return oldestUpdatedAt == null ||
                CachePolicy.isCacheExpired(oldestUpdatedAt, timeout)
    }

    suspend fun updateUpdatedAt(productId: Int, updatedAt: Long) {
        productDao.updateUpdatedAt(
            id = productId,
            updatedAt = updatedAt
        )
    }

    suspend fun updateCategoryUpdatedAt(category: String, updatedAt: Long) {
        productDao.updateCategoryUpdatedAt(
            category = category,
            updatedAt = updatedAt
        )
    }
}