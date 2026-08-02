package com.example.app_retrofit2.data.local.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import com.example.app_retrofit2.data.local.room.entity.CacheInfoEntity
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CacheInfoDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var cacheInfoDao: CacheInfoDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        cacheInfoDao = database.cacheInfoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCacheInfo_inserts_entity() = runTest {
        val cacheInfo = CacheInfoEntity(
            key = "products",
            lastUpdated = 123456789L
        )
        cacheInfoDao.insertCacheInfo(cacheInfo)
        val result = cacheInfoDao.getCacheInfo("products")
        TestCase.assertEquals(cacheInfo, result)
    }

    @Test
    fun getCacheInfo_returns_null_when_not_found() = runTest {
        val result = cacheInfoDao.getCacheInfo("unknown")
        TestCase.assertNull(result)
    }

    @Test
    fun insertCacheInfo_replaces_existing_entity() = runTest {
        cacheInfoDao.insertCacheInfo(
            CacheInfoEntity(
                key = "products",
                lastUpdated = 100L
            )
        )
        cacheInfoDao.insertCacheInfo(
            CacheInfoEntity(
                key = "products",
                lastUpdated = 500L
            )
        )
        val result = cacheInfoDao.getCacheInfo("products")
        TestCase.assertEquals(500L, result?.lastUpdated)
    }
}