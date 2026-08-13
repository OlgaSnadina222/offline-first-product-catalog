package com.example.app_retrofit2.domain.usecase.favorites_screen

import com.example.app_retrofit2.domain.model.Product
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
class GetFavoriteProductsUseCaseTest {
    @Mock
    lateinit var repository: ProductRepo
    private lateinit var useCase: GetFavoriteProductsUseCase

    @Before
    fun setup() {
        useCase = GetFavoriteProductsUseCase(repository)
    }

    @Test
    fun invoke_returns_favorite_products_flow() = runTest {
        val products = listOf(
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
                images = listOf("image1.jpg"),
                isFavorite = true
            )
        )
        whenever(repository.getFavoriteProducts())
            .thenReturn(flowOf(products))

        val result = useCase().first()
        assertEquals(products, result)
        verify(repository).getFavoriteProducts()
    }
}