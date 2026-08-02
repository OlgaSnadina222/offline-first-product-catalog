package com.example.app_retrofit2.domain.usecase.product_details_screen

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ObserveProductByIdUseCaseTest {
    @Mock
    lateinit var repository: ProductRepo
    private lateinit var useCase: ObserveProductByIdUseCase
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
        useCase = ObserveProductByIdUseCase(repository)
    }

    @Test
    fun invoke_returns_product_flow_from_repository() = runTest {
        whenever(repository.observeProductById(1))
            .thenReturn(flowOf(testProduct))
        val result = useCase(1).first()
        assertEquals(testProduct, result)
        verify(repository).observeProductById(1)
    }
}