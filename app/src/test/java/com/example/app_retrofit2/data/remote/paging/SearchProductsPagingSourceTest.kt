package com.example.app_retrofit2.data.remote.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.dto.ProductDto
import com.example.app_retrofit2.domain.model.Product
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class SearchProductsPagingSourceTest {
    @Mock
    lateinit var remote: RemoteProductDataSource
    private lateinit var pagingSource: SearchProductsPagingSource

    @Before
    fun setup() {
        pagingSource = SearchProductsPagingSource(
            remoteRepo = remote,
            query = "phone"
        )
    }

    @Test
    fun load_returns_page_when_api_success() = runTest {
        val dto = ProductDto(
            id = 1,
            title = "Phone",
            description = "Description",
            category = "electronics",
            price = 1000f,
            discountPercentage = 10f,
            rating = 4.8f,
            stock = 10,
            brand = "Samsung",
            images = listOf("1.jpg")
        )
        whenever(remote.searchProducts(
                query = "phone",
                limit = 20,
                skip = 0
            )
        ).thenReturn(Result.success(listOf(dto)))

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        assertTrue(result is PagingSource.LoadResult.Page)

        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals(1, page.data.first().id)
        assertNull(page.prevKey)
        assertEquals(20, page.nextKey)
    }

    @Test
    fun load_returns_null_nextKey_when_list_is_empty() = runTest {
        whenever(remote.searchProducts(
                query = "phone",
                limit = 20,
                skip = 0
            )
        ).thenReturn(Result.success(emptyList()))

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        assertTrue(result is PagingSource.LoadResult.Page)

        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.nextKey)
    }

    @Test
    fun load_returns_error_when_api_fails() = runTest {
        val exception = IOException("Network error")
        whenever(remote.searchProducts(
                query = "phone",
                limit = 20,
                skip = 0
            )
        ).thenReturn(Result.failure(exception))

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(
            exception,
            (result as PagingSource.LoadResult.Error).throwable
        )
    }

    @Test
    fun getRefreshKey_returns_null() {
        val state = PagingState<Int, Product>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(20),
            leadingPlaceholderCount = 0
        )
        assertNull(pagingSource.getRefreshKey(state))
    }
}