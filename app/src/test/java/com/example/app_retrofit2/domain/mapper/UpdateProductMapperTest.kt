package com.example.app_retrofit2.domain.mapper

import com.example.app_retrofit2.data.remote.dto.UpdateProductDto
import com.example.app_retrofit2.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateProductMapperTest {
    @Test
    fun product_toUpdateDto_maps_correctly() {
        val product = Product(
            id = 1,
            title = "Phone",
            description = "Description",
            category = "electronics",
            price = 999f,
            discountPercentage = 10f,
            rating = 4.8f,
            stock = 15,
            brand = "Samsung",
            images = listOf("1.jpg"),
            isFavorite = false
        )

        val expected = UpdateProductDto(
            title = "Phone",
            description = "Description",
            price = 999f,
            rating = 4.8f,
            stock = 15,
            brand = "Samsung"
        )

        assertEquals(expected, product.toUpdateDto())
    }
}