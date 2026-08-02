package com.example.app_retrofit2.data.local.room.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.sync.SyncStatus
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var productDao: ProductDao
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
        images = emptyList(),
        isVisible = true,
        isDeleted = false,
        syncStatus = SyncStatus.PENDING,
        updatedAt = 3000L
    )

    private val testProduct2 = testProduct.copy(
        id = 2,
        title = "Lipstick",
        price = 22.0f
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        productDao = db.productDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertProducts_inserts_products() = runTest {
        productDao.insertProducts(listOf(testProduct))
        val result = productDao.getProducts().first()
        assertEquals(1, result.size)
        assertEquals(testProduct.id, result.first().id)
        assertEquals(testProduct.title, result.first().title)
        assertEquals(testProduct.price, result.first().price)
    }

    @Test
    fun getProducts_returns_all_products() = runTest {
        productDao.insertProducts(
            listOf(
                testProduct,
                testProduct2
            )
        )
        val result = productDao.getProducts().first()
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == 1 })
        assertTrue(result.any { it.id == 2 })
    }

    @Test
    fun getProductById_returns_product() = runTest {
        productDao.insertProducts(
            listOf(testProduct)
        )
        val result = productDao.getProductById(1)
        assertNotNull(result)
        assertEquals(1, result!!.product.id)
        assertEquals(
            "Mascara Waterproof",
            result.product.title
        )
        assertEquals(
            "beauty",
            result.product.category
        )
    }

    @Test
    fun updateProduct_updates_product() = runTest {
        productDao.insertProducts(listOf(testProduct))
        val updatedProduct = testProduct.copy(
            title = "New Mascara",
            price = 25.99f
        )
        productDao.updateProduct(updatedProduct)
        val result = productDao.getProductById(1)
        assertNotNull(result)
        assertEquals(
            "New Mascara",
            result!!.product.title
        )
        assertEquals(
            25.99f,
            result.product.price
        )
    }

    @Test
    fun softDelete_marks_product_as_deleted() = runTest {
        productDao.insertProducts(listOf(testProduct))
        productDao.softDelete(1)
        val result = productDao.getProductById(1)
        assertNotNull(result)
        assertTrue(
            result!!.product.isDeleted
        )
        assertEquals(
            SyncStatus.PENDING,
            result.product.syncStatus
        )
    }

    @Test
    fun hardDelete_removes_product() = runTest {
        productDao.insertProducts(listOf(testProduct))
        assertEquals(
            1,
            productDao.getProductsCount()
        )
        productDao.hardDelete(1)
        assertEquals(
            0,
            productDao.getProductsCount()
        )
        assertNull(
            productDao.getProductById(1)
        )
    }

    @Test
    fun getProductsCount_returns_correct_count() = runTest {
        productDao.insertProducts(
            listOf(
                testProduct,
                testProduct.copy(id = 2),
                testProduct.copy(id = 3)
            )
        )
        val count = productDao.getProductsCount()
        assertEquals(3, count)
    }

    @Test
    fun updateUpdatedAt_timestamp() = runTest {
        productDao.insertProducts(
            listOf(testProduct)
        )
        val newTime = 999999L
        productDao.updateUpdatedAt(
            id = 1,
            updatedAt = newTime
        )
        val result = productDao.getProductById(1)
        assertNotNull(result)
        assertEquals(
            newTime,
            result!!.product.updatedAt
        )
    }

    @Test
    fun updateCategoryUpdatedAt_only_selected_category() = runTest {
        productDao.insertProducts(
            listOf(
                testProduct.copy(
                    id = 1,
                    category = "beauty",
                    updatedAt = 100L
                ),
                testProduct.copy(
                    id = 2,
                    category = "beauty",
                    updatedAt = 200L
                ),
                testProduct.copy(
                    id = 3,
                    category = "fragrances",
                    updatedAt = 300L
                )
            )
        )
        productDao.updateCategoryUpdatedAt(
            category = "beauty",
            updatedAt = 999L
        )
        val products = productDao.getProducts().first()
        val beautyProducts =
            products.filter {
                it.category == "beauty"
            }

        val fragranceProduct =
            products.first {
                it.category == "fragrances"
            }
        assertEquals(
            999L,
            beautyProducts[0].updatedAt
        )
        assertEquals(
            999L,
            beautyProducts[1].updatedAt
        )
        assertEquals(
            300L,
            fragranceProduct.updatedAt
        )
    }

    @Test
    fun getOldestUpdatedAt_returns_oldest_timestamp() = runTest {
        productDao.insertProducts(
            listOf(
                testProduct.copy(
                    id = 1,
                    updatedAt = 300L,
                    category = "beauty"
                ),
                testProduct.copy(
                    id = 2,
                    updatedAt = 100L,
                    category = "beauty"
                ),
                testProduct.copy(
                    id = 3,
                    updatedAt = 500L,
                    category = "beauty"
                )
            )
        )
        val result = productDao.getOldestUpdatedAt("beauty")
        assertEquals(100L, result)
    }

    @Test
    fun observeProductById_returns_product() = runTest {
        productDao.insertProducts(
            listOf(testProduct)
        )
        val result = productDao.observeProductById(1).first()
        assertNotNull(result)
        assertEquals(
            1,
            result!!.product.id
        )
    }

    @Test
    fun pagingSource_returns_products() = runTest {
        productDao.insertProducts(
            listOf(
                testProduct,
                testProduct.copy(id = 2),
                testProduct.copy(id = 3)
            )
        )
        val pagingSource = productDao.pagingSource("all")
        val result = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 10,
                    placeholdersEnabled = false
                )
            )
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(3, page.data.size)
    }
}