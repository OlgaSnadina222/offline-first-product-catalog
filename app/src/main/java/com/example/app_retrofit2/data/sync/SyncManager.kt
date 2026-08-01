package com.example.app_retrofit2.data.sync

import com.example.app_retrofit2.data.local.room.dao.PendingOperationDao
import com.example.app_retrofit2.data.local.room.datasource.LocalProductDataSource
import com.example.app_retrofit2.data.local.room.mapper.toDomain
import com.example.app_retrofit2.data.local.room.mapper.toEntity
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.domain.mapper.toUpdateDto
import jakarta.inject.Inject

class SyncManager @Inject constructor(
    private val pendingOperationDao: PendingOperationDao,
    private val remoteDataSource: RemoteProductDataSource,
    private val localDataSource: LocalProductDataSource
) {
    suspend fun sync() {
        pendingOperationDao.getPendingOperations().forEach { operation ->
            try {
                when (operation.operation) {
                    PendingOperationType.PATCH -> {
                        val product = localDataSource.getProductById(operation.productId) ?: return@forEach
                        remoteDataSource.updateProduct(product.toDomain().toEntity()).getOrThrow()
                        localDataSource.updateProduct(
                            product.copy(syncStatus = SyncStatus.SYNCED)
                        )
                        pendingOperationDao.delete(operation)
                    }
                    PendingOperationType.DELETE -> {
                        remoteDataSource.deleteProduct(operation.productId).getOrThrow()
                        localDataSource.hardDeleteProduct(operation.productId)
                        pendingOperationDao.delete(operation)
                    }
                }

            }
            catch (e: Exception) {
                pendingOperationDao.update(
                    operation.copy(
                        retryCount = operation.retryCount + 1
                    )
                )
            }
        }
    }
}