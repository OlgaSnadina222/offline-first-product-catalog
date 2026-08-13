package com.example.app_retrofit2.data.remote.mapper

import com.example.app_retrofit2.data.remote.dto.CategoryDto
import com.example.app_retrofit2.domain.model.Category
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryDtoMapperTest {

        @Test
        fun dto_toDomain_maps_correctly() {
            val dto = CategoryDto(
                slug = "beauty",
                name = "Beauty"
            )
            val result = dto.toDomain()
            assertEquals(Category(
                slug = "beauty",
                name = "Beauty"
            ),result
            )
        }

        @Test
        fun dto_toDomain_uses_default_values_when_fields_are_null() {
            val dto = CategoryDto(
                slug = null,
                name = null
            )
            val result = dto.toDomain()
            assertEquals(Category(
                    slug = "unknown",
                    name = "unknown"
                ), result
            )
        }
}