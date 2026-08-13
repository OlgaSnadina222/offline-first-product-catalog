package com.example.app_retrofit2.domain.usecase.products_screen

import androidx.paging.PagingData
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetPagedProductsUseCaseTest {

    @Mock
    lateinit var repository: ProductRepo
    private lateinit var useCase: GetPagedProductsUseCase

    @Before
    fun setup() {
        useCase = GetPagedProductsUseCase(repository)
    }

    @Test
    fun invoke_returns_paging_data_from_repository() = runTest {
        val pagingData = PagingData.from(
            listOf(
                Product(
                    id = 1,
                    title = "Phone",
                    description = "Description",
                    category = "electronics",
                    price = 999f,
                    discountPercentage = 10f,
                    rating = 4.8f,
                    stock = 10,
                    brand = "Samsung",
                    images = listOf("image.jpg"),
                    isFavorite = false
                )
            )
        )
        whenever(
            repository.getPagedProducts(
                category = "electronics",
                sort = ProductSort.PRICE_ASC
            )
        ).thenReturn(flowOf(pagingData))

        val result = useCase(
            category = "electronics",
            sort = ProductSort.PRICE_ASC
        ).first()

        assertEquals(pagingData, result)
        verify(repository).getPagedProducts(
            category = "electronics",
            sort = ProductSort.PRICE_ASC
        )
    }
}