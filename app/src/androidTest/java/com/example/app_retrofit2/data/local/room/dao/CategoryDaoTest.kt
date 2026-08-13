package com.example.app_retrofit2.data.local.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import com.example.app_retrofit2.data.local.room.entity.CategoryEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

class CategoryDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var productDao: ProductDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        categoryDao = db.categoryDao()
        productDao = db.productDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertCategories_inserts_all_categories() = runTest {
        val categories = listOf(
            CategoryEntity("beauty", "Beauty"),
            CategoryEntity("smartphones", "Smartphones")
        )
        categoryDao.insertCategories(categories)
        val result = categoryDao.getCategories()
        assertEquals(2, result.size)
        assertEquals(categories, result)
    }

    @Test
    fun getCategories_returns_empty_list_when_database_is_empty() = runTest {
        val result = categoryDao.getCategories()
        assertTrue(result.isEmpty())
    }

    @Test
    fun clearCategories_removes_all_categories() = runTest {
        categoryDao.insertCategories(
            listOf(
                CategoryEntity("smartphones", "Smartphones"),
                CategoryEntity("beauty", "Beauty")
            )
        )
        categoryDao.clearCategories()
        val result = categoryDao.getCategories()
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertCategories_replaces_existing_category() = runTest {
        categoryDao.insertCategories(
            listOf(
                CategoryEntity("smartphones", "Smartphones")
            )
        )
        categoryDao.insertCategories(
            listOf(
                CategoryEntity("smartphones", "Mobile phones")
            )
        )
        val result = categoryDao.getCategories()
        assertEquals(1, result.size)
        assertEquals("Mobile phones", result.first().name)
        assertEquals("smartphones", result.first().slug)
    }
}