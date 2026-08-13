package com.example.app_retrofit2.data.local.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import com.example.app_retrofit2.data.local.room.entity.FavoriteEntity
import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.sync.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FavoriteDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var favoriteDao: FavoriteDao
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
        images = null,
        isVisible = true,
        isDeleted = false,
        syncStatus = SyncStatus.PENDING,
        updatedAt = 3000L
    )
    private suspend fun insertTestProduct() {
        productDao.insertProducts(listOf(testProduct))
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
        favoriteDao = db.favoriteDao()
        productDao = db.productDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addFavorite_inserts_favorite() = runTest {
        insertTestProduct()
        favoriteDao.addFavorite(
            FavoriteEntity(productId = 1)
        )
        assertTrue(favoriteDao.exists(1))
    }

    @Test
    fun removeFavorite_deletes_favorite() = runTest {
        insertTestProduct()
        favoriteDao.addFavorite(
            FavoriteEntity(productId = 1)
        )
        favoriteDao.removeFavorite(1)
        assertFalse(favoriteDao.exists(1))
    }

    @Test
    fun exists_returns_false_when_not_found() = runTest {
        assertFalse(favoriteDao.exists(100))
    }

    @Test
    fun getFavoriteProducts_returns_products() = runTest {
        productDao.insertProducts(
            listOf(testProduct)
        )
        favoriteDao.addFavorite(FavoriteEntity(1))
        val result = favoriteDao.getFavoriteProducts().first()
        assertEquals(1, result.size)
        assertEquals(1, result.first().product.id)
    }

}