package com.example.app_retrofit2.data.repository

import com.example.app_retrofit2.data.local.room.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.room.dao.PendingOperationDao
import com.example.app_retrofit2.data.local.room.datasource.LocalProductDataSource
import com.example.app_retrofit2.data.local.room.mapper.toEntity
import com.example.app_retrofit2.data.local.room.transaction.DatabaseTransaction
import com.example.app_retrofit2.data.paging.CATEGORY
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.dto.ProductDto
import com.example.app_retrofit2.data.sync.SyncScheduler
import com.example.app_retrofit2.domain.model.Product
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ProductRepositoryImplTest {
    @Mock
    lateinit var remote: RemoteProductDataSource
    @Mock
    lateinit var local: LocalProductDataSource
    @Mock
    lateinit var cacheInfoDao: CacheInfoDao
    @Mock
    lateinit var pendingOperationDao: PendingOperationDao
    @Mock
    lateinit var syncScheduler: SyncScheduler
    @Mock
    lateinit var transaction: DatabaseTransaction

    private lateinit var productRepositoryImpl: ProductRepositoryImpl

    val product1 = Product(
        id = 1,
        title = "iPhone 15",
        description = "Latest Apple smartphone",
        category = "smartphones",
        price = 999.99f,
        discountPercentage = 10.0f,
        rating = 4.8f,
        stock = 25,
        brand = "Apple",
        images = listOf(
            "https://example.com/iphone15.jpg"
        ),
        isFavorite = false
    )
    val dtoProduct = ProductDto(
        id = 2,
        title = "Galaxy S24",
        description = "Samsung flagship smartphone",
        category = "smartphones",
        price = 899.99f,
        discountPercentage = 15.0f,
        rating = 4.7f,
        stock = 40,
        brand = "Samsung",
        images = listOf(
            "https://example.com/galaxy-s24.jpg"
        )
    )

    @Before
    fun setup() = runTest {
        productRepositoryImpl = ProductRepositoryImpl(
            remoteProductDataSource = remote,
            localProductDataSource = local,
            cacheInfoDao = cacheInfoDao,
            pendingOperationDao = pendingOperationDao,
            syncScheduler = syncScheduler,
            transaction = transaction
        )
        whenever(transaction.withTransaction<Unit>(any()))
            .thenAnswer { invocationOnMock ->
                val operation = invocationOnMock.getArgument<suspend  () -> Unit>(0)
                runBlocking {
                    operation()
                }
            }
    }

    @Test
    fun insertProductToLocalDataSource() = runTest {
        productRepositoryImpl.insertProducts(listOf(product1))
        verify(local).insertProducts(
            listOf(product1.toEntity())
        )
    }

    @Test
    fun clearProductFromLocalDataSource() = runTest {
        productRepositoryImpl.clearProducts(CATEGORY)
        verify(local).clearProducts(CATEGORY)
    }

    @Test
    fun getProductByIdFromLocalWhenExist() = runTest {
        val localProduct = product1.toEntity()
        whenever(local.getProductById(1))
            .thenReturn(localProduct)
        val result = productRepositoryImpl.getProductById(1)
        assertTrue(result.isSuccess)
        assertEquals(product1,
            result.getOrThrow())
        verify(remote, never()).getProductById(1)
    }

    @Test
    fun insertProductFromRemoteIfNotExist() = runTest {
        whenever(local.getProductById(2))
            .thenReturn(null)
        whenever(remote.getProductById(2))
            .thenReturn(Result.success(dtoProduct))
        productRepositoryImpl.getProductById(2)
        verify(local).insertProducts(listOf(any()))
    }

    @Test
    fun checkProductIsFavorite() = runTest {
        whenever(local.isFavorite(1))
            .thenReturn(true)
        val result = productRepositoryImpl.toggleFavorite(1)
        verify(local).isFavorite(1)
    }

}