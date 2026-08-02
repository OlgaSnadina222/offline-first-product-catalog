package com.example.app_retrofit2.data.local.room.mapper

import com.example.app_retrofit2.data.local.room.entity.CategoryEntity
import com.example.app_retrofit2.domain.model.Category
import junit.framework.TestCase.assertEquals
import org.junit.Test

class LocalCategoryMapperTest {

    @Test
    fun entity_toDomain_maps_correctly() {
        val entity = CategoryEntity(
            slug = "beauty",
            name = "Beauty"
        )
        val expected = Category(
            slug = "beauty",
            name = "Beauty"
        )
        assertEquals(expected, entity.toDomain())
    }

    @Test
    fun domain_toEntity_maps_correctly() {
        val category = Category(
            slug = "beauty",
            name = "Beauty"
        )
        val expected = CategoryEntity(
            slug = "beauty",
            name = "Beauty"
        )
        assertEquals(expected, category.toEntity())
    }

}
