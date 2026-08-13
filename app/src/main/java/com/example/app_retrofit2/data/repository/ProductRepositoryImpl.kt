package com.example.app_retrofit2.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.app_retrofit2.data.local.room.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.room.dao.PendingOperationDao
import com.example.app_retrofit2.data.local.room.datasource.LocalProductDataSource
import com.example.app_retrofit2.data.local.room.entity.PendingOperationEntity
import com.example.app_retrofit2.data.local.room.mapper.toDomain
import com.example.app_retrofit2.data.local.room.mapper.toEntity
import com.example.app_retrofit2.data.local.room.transaction.DatabaseTransaction
import com.example.app_retrofit2.data.paging.ProductRemoteMediator
import com.example.app_retrofit2.data.remote.paging.SearchProductsPagingSource
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toEntity
import com.example.app_retrofit2.data.sync.PendingOperationType
import com.example.app_retrofit2.data.sync.SyncScheduler
import com.example.app_retrofit2.data.sync.SyncStatus
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteProductDataSource: RemoteProductDataSource,
    private val localProductDataSource: LocalProductDataSource,
    private val cacheInfoDao: CacheInfoDao,
    private val pendingOperationDao: PendingOperationDao,
    private val transaction: DatabaseTransaction,
    private val syncScheduler: SyncScheduler

) : ProductRepo {

    override suspend fun insertProducts(products: List<Product>) {
        localProductDataSource.insertProducts(products.map { it.toEntity() })
    }

    override suspend fun clearProducts(category: String) {
        localProductDataSource.clearProducts(category)
    }

    override fun searchProducts(query: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                SearchProductsPagingSource(
                    remoteRepo = remoteProductDataSource,
                    query = query
                )
            }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedProducts(category: String, sort: ProductSort): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = ProductRemoteMediator(
                remote = remoteProductDataSource,
                local = localProductDataSource,
                category = category,
                cacheInfoDao = cacheInfoDao,
                transaction = transaction
            ),
            pagingSourceFactory = {
                localProductDataSource.pagingSource(
                    category = category,
                    sort = sort
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { productWithFavorite ->
                productWithFavorite.product.toDomain()
                    .copy(isFavorite = productWithFavorite.favorite != null)
            }
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        val localProduct = localProductDataSource.getProductById(id)
        if (localProduct != null) {
            return Result.success(localProduct.toDomain())
        }
        return remoteProductDataSource.getProductById(id).map { dto ->
            localProductDataSource.insertProducts(listOf(dto.toEntity()))
            dto.toEntity().toDomain()
        }
    }

    override suspend fun toggleFavorite(productId: Int) {
        if (localProductDataSource.isFavorite(productId)) {
            localProductDataSource.removeFavorite(productId)
        } else {
            localProductDataSource.addFavorite(productId)
        }
    }

    override fun getFavoriteProducts(): Flow<List<Product>> {
        return localProductDataSource.getFavoriteProducts().map { favsWithProducts ->
            favsWithProducts.map { favWithProduct ->
                favWithProduct.product.toDomain().copy(isFavorite = true) }
        }
    }

    override suspend fun updateProduct(product: Product): Result<Product> {
        val now = System.currentTimeMillis()
        transaction.withTransaction {
            localProductDataSource.updateProduct(
                product.toEntity().copy(
                    updatedAt = now,
                    syncStatus = SyncStatus.PENDING
                )
            )
            if (
                !pendingOperationDao.exists(
                    product.id,
                    PendingOperationType.PATCH
                )
            ) {
                pendingOperationDao.insert(
                    PendingOperationEntity(
                        productId = product.id,
                        operation = PendingOperationType.PATCH,
                        retryCount = 0,
                        createdAt = now
                    )
                )
            }
        }
        syncScheduler.syncNow()
        return Result.success(product)
    }

    override fun observeProductById(id: Int): Flow<Product> {
        return localProductDataSource.observeProductById(id)
    }

    override suspend fun deleteProduct(productId: Int): Result<Unit> {
        return try {
            transaction.withTransaction {
                localProductDataSource.removeFavorite(productId)
                localProductDataSource.softDeleteProduct(productId)
                pendingOperationDao.insert(
                    PendingOperationEntity(
                        productId = productId,
                        operation = PendingOperationType.DELETE,
                        retryCount = 0,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
            syncScheduler.syncNow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

