package com.example.app_retrofit2.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.InitializeAction
import com.example.app_retrofit2.data.local.room.dao.CacheInfoDao
import com.example.app_retrofit2.data.local.room.datasource.LocalProductDataSource
import com.example.app_retrofit2.data.local.room.entity.ProductWithFavorite
import com.example.app_retrofit2.data.local.room.transaction.DatabaseTransaction
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.dto.ProductDto
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.Result
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

const val CATEGORY = "smartphones"
@OptIn(ExperimentalPagingApi::class)
@RunWith(MockitoJUnitRunner::class)
class ProductRemoteMediatorTest {
    @Mock
    lateinit var remote: RemoteProductDataSource
    @Mock
    lateinit var local: LocalProductDataSource
    @Mock
    lateinit var cacheInfoDao: CacheInfoDao
    @Mock
    lateinit var transaction: DatabaseTransaction
    @Mock
    lateinit var state: PagingState<Int, ProductWithFavorite>

    private lateinit var mediator: ProductRemoteMediator

    val product1 = ProductDto(
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
        )
    )
    val product2 = ProductDto(
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
        mediator = ProductRemoteMediator(
            remote = remote,
            local = local,
            category = CATEGORY,
            cacheInfoDao = cacheInfoDao,
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
    fun initRefreshMediatorWhenCacheExpired() = runTest {
        whenever(local.isCacheExpired(CATEGORY))
            .thenReturn(true)

        val result = mediator.initialize()
        assertEquals(
            InitializeAction.LAUNCH_INITIAL_REFRESH,
            result
        )
    }

    @Test
    fun refreshLoadFirstPage() = runTest {
        val products = listOf(
            product1,
            product2
        )
        whenever(
            remote.getProductsByCategory(
                category = CATEGORY,
                limit = 20,
                skip = 0
            )
        ).thenReturn(kotlin.Result.success(products))

        whenever(local.getProductById(any()))
            .thenReturn(null)

        whenever(state.config)
            .thenReturn(
                PagingConfig(
                    pageSize = 20
                )
            )
        val result = mediator.load(
            loadType = LoadType.REFRESH,
            state = state
        )

        assertTrue(
            result is RemoteMediator.MediatorResult.Success
        )
        val success = result as RemoteMediator.MediatorResult.Success
        assertTrue(success.endOfPaginationReached)

    }

}