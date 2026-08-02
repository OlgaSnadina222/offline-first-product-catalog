package com.example.app_retrofit2.data.sync

import com.example.app_retrofit2.data.local.room.dao.PendingOperationDao
import com.example.app_retrofit2.data.local.room.datasource.LocalProductDataSource
import com.example.app_retrofit2.data.local.room.entity.PendingOperationEntity
import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toDto
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class SyncManagerTest {
    @Mock
    lateinit var pendingOperationDao: PendingOperationDao
    @Mock
    lateinit var remoteDataSource: RemoteProductDataSource
    @Mock
    lateinit var localDataSource: LocalProductDataSource
    private lateinit var syncManager: SyncManager
    private val testProduct = ProductEntity(
        id = 10,
        title = "Phone",
        description = "Description",
        category = "electronics",
        price = 999f,
        discountPercentage = 10f,
        rating = 4.8f,
        stock = 20,
        brand = "Samsung",
        images = listOf("image1.jpg"),
        isVisible = true,
        isDeleted = false,
        syncStatus = SyncStatus.PENDING,
        updatedAt = 12345L
    )

    @Before
    fun setup() {
        syncManager = SyncManager(
            pendingOperationDao = pendingOperationDao,
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource
        )
    }
    @Test
    fun sync_patch_success_updates_product_and_removes_operation() = runTest {
        val operation = PendingOperationEntity(
            id = 1,
            productId = 10,
            operation = PendingOperationType.PATCH,
            retryCount = 0,
            createdAt = 1L
        )
        whenever(pendingOperationDao.getPendingOperations())
            .thenReturn(listOf(operation))

        whenever(localDataSource.getProductById(10))
            .thenReturn(testProduct)

        whenever(
            remoteDataSource.updateProduct(any())
        ).thenReturn(Result.success(testProduct.toDto()))

        syncManager.sync()
        verify(remoteDataSource).updateProduct(any())
        verify(pendingOperationDao).delete(operation)
    }

    @Test
    fun sync_patch_skips_when_product_not_found() = runTest {
        val operation = PendingOperationEntity(
            id = 1,
            productId = 10,
            operation = PendingOperationType.PATCH,
            retryCount = 0,
            createdAt = 1L
        )
        whenever(
            pendingOperationDao.getPendingOperations()
        ).thenReturn(listOf(operation))
        whenever(
            localDataSource.getProductById(10)
        ).thenReturn(null)
        syncManager.sync()
        verify(remoteDataSource, never()).updateProduct(any())
        verify(pendingOperationDao, never()).delete(any())
    }

    @Test
    fun sync_patch_failure_increments_retry() = runTest {
        val operation = PendingOperationEntity(
            id = 1,
            productId = 10,
            operation = PendingOperationType.PATCH,
            retryCount = 2,
            createdAt = 1L
        )
        whenever(
            pendingOperationDao.getPendingOperations()
        ).thenReturn(listOf(operation))
        whenever(localDataSource.getProductById(10))
            .thenReturn(testProduct)
        whenever(
            remoteDataSource.updateProduct(any())
        ).thenReturn(
            Result.failure(IOException())
        )
        syncManager.sync()
        verify(pendingOperationDao).update(operation.copy(retryCount = 3))
    }

    @Test
    fun sync_delete_success_hard_deletes_product() = runTest {
        val operation = PendingOperationEntity(
            id = 1,
            productId = 10,
            operation = PendingOperationType.DELETE,
            retryCount = 0,
            createdAt = 1L
        )
        whenever(
            pendingOperationDao.getPendingOperations()
        ).thenReturn(listOf(operation))
        whenever(
            remoteDataSource.deleteProduct(10)
        ).thenReturn(Result.success(Unit))
        syncManager.sync()
        verify(localDataSource).hardDeleteProduct(10)
        verify(pendingOperationDao).delete(operation)
    }

    @Test
    fun sync_delete_failure_increments_retry() = runTest {
        val operation = PendingOperationEntity(
            id = 1,
            productId = 10,
            operation = PendingOperationType.DELETE,
            retryCount = 5,
            createdAt = 1L
        )
        whenever(
            pendingOperationDao.getPendingOperations()
        ).thenReturn(listOf(operation))
        whenever(
            remoteDataSource.deleteProduct(10)
        ).thenReturn(
            Result.failure(IOException())
        )
        syncManager.sync()
        verify(pendingOperationDao).update(operation.copy(retryCount = 6))
    }

    @Test
    fun sync_does_nothing_when_queue_is_empty() = runTest {
        whenever(
            pendingOperationDao.getPendingOperations()
        ).thenReturn(emptyList())
        syncManager.sync()
        verifyNoInteractions(remoteDataSource)
    }

    @Test
    fun sync_processes_all_operations() = runTest {
        val patch = PendingOperationEntity(
            id = 1,
            productId = 1,
            operation = PendingOperationType.PATCH,
            retryCount = 0,
            createdAt = 1
        )
        val delete = PendingOperationEntity(
            id = 2,
            productId = 2,
            operation = PendingOperationType.DELETE,
            retryCount = 0,
            createdAt = 2
        )
        whenever(
            pendingOperationDao.getPendingOperations()
        ).thenReturn(listOf(patch, delete))
        whenever(localDataSource.getProductById(1))
            .thenReturn(testProduct)
        whenever(
            remoteDataSource.updateProduct(any())
        ).thenReturn(Result.success(testProduct.toDto()))
        whenever(
            remoteDataSource.deleteProduct(2)
        ).thenReturn(Result.success(Unit))
        syncManager.sync()
        verify(remoteDataSource).updateProduct(any())
        verify(remoteDataSource).deleteProduct(2)
    }
}