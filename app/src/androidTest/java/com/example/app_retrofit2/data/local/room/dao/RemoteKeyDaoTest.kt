package com.example.app_retrofit2.data.local.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import com.example.app_retrofit2.data.local.room.entity.RemoteKeyEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeyDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: RemoteKeyDao
    private val remoteKey = RemoteKeyEntity(
        id = "beauty",
        nextKey = 20
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
        dao = db.remoteKeyDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertRemoteKey_inserts_key() = runTest {
        dao.insertRemoteKey(remoteKey)
        val result = dao.getRemoteKey("beauty")
        assertNotNull(result)
        assertEquals(remoteKey, result)
    }

    @Test
    fun getRemoteKey_returns_null_when_not_found() = runTest {
        val result = dao.getRemoteKey("unknown")
        assertNull(result)
    }

    @Test
    fun insertRemoteKey_replaces_existing_key() = runTest {
        dao.insertRemoteKey(
            RemoteKeyEntity(
                id = "beauty",
                nextKey = 20
            )
        )
        dao.insertRemoteKey(
            RemoteKeyEntity(
                id = "beauty",
                nextKey = 40
            )
        )
        val result = dao.getRemoteKey("beauty")
        assertNotNull(result)
        assertEquals(40, result!!.nextKey)
    }

    @Test
    fun clearRemoteKeys_removes_key() = runTest {
        dao.insertRemoteKey(remoteKey)
        dao.clearRemoteKeys("beauty")
        val result = dao.getRemoteKey("beauty")
        assertNull(result)
    }
}