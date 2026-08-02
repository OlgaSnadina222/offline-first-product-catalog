package com.example.app_retrofit2.data.local.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import com.example.app_retrofit2.data.local.room.entity.CategoryEntity
import com.example.app_retrofit2.data.local.room.entity.ProductCategoryCrossRef
import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.sync.SyncStatus
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductCategoryCrossRefDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: ProductCategoryCrossRefDao
    private lateinit var productDao: ProductDao
    private lateinit var categoryDao: CategoryDao
    private val testProduct = ProductEntity(
        id = 1,
        title = "Mascara Waterproof",
        description = "Perfect every day mascara for beauty eyelashes.",
        category = "beauty",
        price = 15.50f,
        discountPercentage = 0f,
        rating = 4.9f,
        stock = 78,
        brand = "MaxMara",
        images = null,
        isVisible = true,
        isDeleted = false,
        syncStatus = SyncStatus.PENDING,
        updatedAt = 3000L
    )
    private suspend fun insertTestData() {
        productDao.insertProducts(
            listOf(
                testProduct,
                testProduct.copy(id = 2),
                testProduct.copy(id = 3)
            )
        )

        categoryDao.insertCategories(
            listOf(
                CategoryEntity(
                    slug = "beauty",
                    name = "Beauty"
                ),
                CategoryEntity(
                    slug = "fragrances",
                    name = "Fragrances"
                )
            )
        )
    }

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.productCategoryCrossRefDao()
        productDao = db.productDao()
        categoryDao = db.categoryDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertCrossRefs_inserts_cross_refs() = runTest {
        val refs = listOf(
            ProductCategoryCrossRef(1, "beauty"),
            ProductCategoryCrossRef(2, "beauty")
        )
        insertTestData()
        dao.insertCrossRefs(refs)
        val result = dao.getAllCrossRefs()
        assertEquals(2, result.size)
        assertEquals(refs.toSet(), result.toSet())
    }

    @Test
    fun clearCrossRefs_removes_category_cross_refs() = runTest {
        insertTestData()
        dao.insertCrossRefs(
            listOf(
                ProductCategoryCrossRef(1, "beauty"),
                ProductCategoryCrossRef(2, "beauty"),
                ProductCategoryCrossRef(3, "fragrances")
            )
        )
        dao.clearCrossRefs("beauty")
        val result = dao.getAllCrossRefs()
        assertEquals(1, result.size)
        assertEquals("fragrances", result.first().categorySlug)
    }
}