package com.example.app_retrofit2.domain.usecase.product_details_screen

import com.example.app_retrofit2.domain.model.Product
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ValidateProductUseCaseTest {
    private lateinit var useCase: ValidateProductUseCase
    private val validProduct = Product(
        id = 1,
        title = "Phone",
        description = "Great phone",
        category = "electronics",
        price = 999f,
        discountPercentage = 10f,
        rating = 4.5f,
        stock = 20,
        brand = "Samsung",
        images = listOf("image.jpg"),
        isFavorite = false
    )

    @Before
    fun setup() {
        useCase = ValidateProductUseCase()
    }

    @Test
    fun invoke_returns_success_for_valid_product() {
        val result = useCase(validProduct)
        assertTrue(result.isSuccess)
    }

    @Test
    fun invoke_fails_when_title_is_blank() {
        val product = validProduct.copy(title = "")
        val result = useCase(product)
        assertTrue(result.isFailure)
        assertEquals(
            "Title cannot be empty.",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun invoke_fails_when_brand_is_blank() {
        val product = validProduct.copy(brand = "")
        val result = useCase(product)
        assertTrue(result.isFailure)
        assertEquals(
            "Brand cannot be empty.",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun invoke_fails_when_price_is_zero() {
        val product = validProduct.copy(price = 0f)
        val result = useCase(product)
        assertTrue(result.isFailure)
        assertEquals(
            "Price must be greater than 0.",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun invoke_fails_when_stock_is_negative() {
        val product = validProduct.copy(stock = -1)
        val result = useCase(product)
        assertTrue(result.isFailure)
        assertEquals(
            "Stock cannot be negative.",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun invoke_fails_when_rating_is_out_of_range() {
        val product = validProduct.copy(rating = 5.5f)
        val result = useCase(product)
        assertTrue(result.isFailure)
        assertEquals(
            "Rating must be between 0 and 5.",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun invoke_fails_when_description_is_blank() {
        val product = validProduct.copy(description = "")
        val result = useCase(product)
        assertTrue(result.isFailure)
        assertEquals(
            "Description cannot be empty.",
            result.exceptionOrNull()?.message
        )
    }
}