package com.example.app_retrofit2.data.local.room.mapper

import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductMapperTest {

    @Test
    fun entity_toDomain_maps_correctly() {
        val entity = ProductEntity(
            id = 1,
            title = "Phone",
            description = "Description",
            category = "electronics",
            price = 1000f,
            discountPercentage = 10f,
            rating = 4.8f,
            stock = 15,
            brand = "Samsung",
            images = listOf("image1.jpg")
        )
        val result = entity.toDomain()
        assertEquals(1, result.id)
        assertEquals("Phone", result.title)
        assertEquals("Description", result.description)
        assertEquals("electronics", result.category)
        assertEquals(1000f, result.price)
        assertEquals(10f, result.discountPercentage)
        assertEquals(4.8f, result.rating)
        assertEquals(15, result.stock)
        assertEquals("Samsung", result.brand)
        assertEquals(listOf("image1.jpg"), result.images)
    }

    @Test
    fun entity_toDomain_uses_default_values_when_fields_are_null() {
        val entity = ProductEntity(
            id = 1,
            title = null,
            description = null,
            category = null,
            price = null,
            discountPercentage = 0f,
            rating = 0f,
            stock = 0,
            brand = null,
            images = null
        )
        val result = entity.toDomain()
        assertEquals("", result.title)
        assertEquals("", result.description)
        assertEquals("unknown", result.category)
        assertEquals(0f, result.price)
        assertEquals("unbranded", result.brand)
        assertEquals(emptyList<String>(), result.images)
    }

    @Test
    fun domain_toEntity_maps_correctly() {
        val product = Product(
            id = 1,
            title = "Phone",
            description = "Description",
            category = "electronics",
            price = 1000f,
            discountPercentage = 10f,
            rating = 4.8f,
            stock = 15,
            brand = "Samsung",
            images = listOf("image1.jpg")
        )
        val result = product.toEntity()
        assertEquals(product.id, result.id)
        assertEquals(product.title, result.title)
        assertEquals(product.description, result.description)
        assertEquals(product.category, result.category)
        assertEquals(product.price, result.price)
        assertEquals(product.discountPercentage, result.discountPercentage)
        assertEquals(product.rating, result.rating)
        assertEquals(product.stock, result.stock)
        assertEquals(product.brand, result.brand)
        assertEquals(product.images, result.images)
    }

    @Test
    fun domain_toEntity_preserves_all_fields() {
        val product = Product(
            id = 5,
            title = "Laptop",
            description = "Gaming laptop",
            category = "laptops",
            price = 2500f,
            discountPercentage = 15f,
            rating = 4.9f,
            stock = 7,
            brand = "MSI",
            images = listOf("1.jpg", "2.jpg")
        )
        assertEquals(product, product.toEntity().toDomain())
    }
}