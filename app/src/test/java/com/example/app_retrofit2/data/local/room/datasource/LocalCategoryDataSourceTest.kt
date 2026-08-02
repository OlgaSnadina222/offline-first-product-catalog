package com.example.app_retrofit2.data.local.room.datasource

import com.example.app_retrofit2.data.local.room.dao.CategoryDao
import com.example.app_retrofit2.data.local.room.entity.CategoryEntity
import com.example.app_retrofit2.domain.model.Category
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class LocalCategoryDataSourceTest {
    @Mock
    lateinit var categoryDao: CategoryDao
    private lateinit var dataSource: LocalCategoryDataSource

    @Before
    fun setup() {
        dataSource = LocalCategoryDataSource(categoryDao)
    }

    @Test
    fun getCategories_returns_categories_from_dao() = runTest {
        val entities = listOf(
            CategoryEntity("beauty", "Beauty"),
            CategoryEntity("smartphones", "Smartphones")
        )
        whenever(categoryDao.getCategories())
            .thenReturn(entities)
        val result = dataSource.getCategories()
        assertEquals(entities, result)
        verify(categoryDao).getCategories()
    }

    @Test
    fun insertCategories_maps_domain_to_entity_and_calls_dao() = runTest {
        val categories = listOf(
            Category(
                slug = "beauty",
                name = "Beauty"
            ),
            Category(
                slug = "smartphones",
                name = "Smartphones"
            )
        )
        dataSource.insertCategories(categories)
        verify(categoryDao).insertCategories(
            listOf(
                CategoryEntity("beauty", "Beauty"),
                CategoryEntity("smartphones", "Smartphones")
            )
        )
    }

    @Test
    fun clearCategories_calls_dao() = runTest {
        dataSource.clearCategories()
        verify(categoryDao).clearCategories()
    }
}