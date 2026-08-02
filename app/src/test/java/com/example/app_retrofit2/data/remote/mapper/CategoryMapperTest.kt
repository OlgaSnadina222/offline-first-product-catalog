package com.example.app_retrofit2.data.remote.mapper

import com.example.app_retrofit2.data.remote.dto.CategoryDto
import com.example.app_retrofit2.domain.model.Category
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class CategoryMapperTest {
    @RunWith(JUnit4::class)
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
}