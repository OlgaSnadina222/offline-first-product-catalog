package com.example.app_retrofit2.domain.usecase.product_details_screen

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetProductByIdUseCaseTest {
    @Mock
    lateinit var repository: ProductRepo
    private lateinit var useCase: GetProductByIdUseCase
    private val testProduct = Product(
        id = 1,
        title = "Phone",
        description = "Description",
        category = "electronics",
        price = 999f,
        discountPercentage = 10f,
        rating = 4.8f,
        stock = 15,
        brand = "Samsung",
        images = listOf("image1.jpg"),
        isFavorite = false
    )

    @Before
    fun setup() {
        useCase = GetProductByIdUseCase(repository)
    }

    @Test
    fun invoke_returns_product_from_repository() = runTest {
        whenever(repository.getProductById(1))
            .thenReturn(Result.success(testProduct))
        val result = useCase(1)
        assertTrue(result.isSuccess)
        assertEquals(testProduct, result.getOrNull())
        verify(repository).getProductById(1)
    }
}