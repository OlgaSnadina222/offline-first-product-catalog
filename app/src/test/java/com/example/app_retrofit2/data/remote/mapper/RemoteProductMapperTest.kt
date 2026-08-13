package com.example.app_retrofit2.data.remote.mapper

import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.remote.dto.ProductDto
import com.example.app_retrofit2.data.sync.SyncStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductDtoMapperTest {

    @Test
    fun dto_toEntity_maps_correctly() {
        val before = System.currentTimeMillis()
        val dto = ProductDto(
            id = 1,
            title = "Phone",
            description = "Description",
            category = "electronics",
            price = 999f,
            discountPercentage = 10f,
            rating = 4.8f,
            stock = 15,
            brand = "Samsung",
            images = listOf("1.jpg", "2.jpg")
        )
        val result = dto.toEntity()
        val after = System.currentTimeMillis()
        assertEquals(1, result.id)
        assertEquals("Phone", result.title)
        assertEquals("Description", result.description)
        assertEquals("electronics", result.category)
        assertEquals(999f, result.price)
        assertEquals(10f, result.discountPercentage)
        assertEquals(4.8f, result.rating)
        assertEquals(15, result.stock)
        assertEquals("Samsung", result.brand)
        assertEquals(listOf("1.jpg", "2.jpg"), result.images)
        assertTrue(result.isVisible)
        assertFalse(result.isDeleted)
        assertEquals(SyncStatus.SYNCED, result.syncStatus)
        assertTrue(result.updatedAt in before..after)
    }

    @Test
    fun dto_toEntity_uses_default_values_when_fields_are_null() {
        val dto = ProductDto(
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
        val result = dto.toEntity()
        assertEquals("", result.title)
        assertEquals("", result.description)
        assertEquals("unknown", result.category)
        assertEquals(0f, result.price)
        assertEquals("unbranded", result.brand)
        assertEquals(emptyList<String>(), result.images)
        assertTrue(result.isVisible)
        assertFalse(result.isDeleted)
        assertEquals(SyncStatus.SYNCED, result.syncStatus)
    }

    @Test
    fun entity_toDto_maps_correctly() {
        val entity = ProductEntity(
            id = 1,
            title = "Phone",
            description = "Description",
            category = "electronics",
            price = 999f,
            discountPercentage = 10f,
            rating = 4.8f,
            stock = 15,
            brand = "Samsung",
            images = listOf("1.jpg", "2.jpg"),
            isVisible = true,
            isDeleted = false,
            syncStatus = SyncStatus.SYNCED,
            updatedAt = 123456L
        )
        val result = entity.toDto()
        assertEquals(dtoExpected(), result)
    }

    @Test
    fun entity_toDto_and_back_preserves_business_fields() {
        val dto = ProductDto(
            id = 5,
            title = "Laptop",
            description = "Gaming",
            category = "laptops",
            price = 2500f,
            discountPercentage = 5f,
            rating = 4.9f,
            stock = 3,
            brand = "MSI",
            images = listOf("1.jpg")
        )
        val mapped = dto.toEntity().toDto()
        assertEquals(dto, mapped)
    }

    private fun dtoExpected() = ProductDto(
        id = 1,
        title = "Phone",
        description = "Description",
        category = "electronics",
        price = 999f,
        discountPercentage = 10f,
        rating = 4.8f,
        stock = 15,
        brand = "Samsung",
        images = listOf("1.jpg", "2.jpg")
    )
}