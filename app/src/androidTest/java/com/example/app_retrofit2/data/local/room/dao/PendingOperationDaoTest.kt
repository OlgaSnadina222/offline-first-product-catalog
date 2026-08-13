package com.example.app_retrofit2.data.local.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import com.example.app_retrofit2.data.local.room.entity.PendingOperationEntity
import com.example.app_retrofit2.data.sync.PendingOperationType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PendingOperationDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: PendingOperationDao
    private val operation = PendingOperationEntity(
        id = 0,
        productId = 1,
        operation = PendingOperationType.PATCH,
        retryCount = 0,
        createdAt = 1000L
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
        dao = db.pendingOperationDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insert_operation() = runTest {
        dao.insert(operation)
        assertTrue(
            dao.exists(
                productId = 1,
                operation = PendingOperationType.PATCH
            )
        )
    }

    @Test
    fun delete_removing_operation() = runTest {
        dao.insert(operation)
        val inserted = dao.getPendingOperations().first()
        dao.delete(inserted)
        assertFalse(
            dao.exists(
                productId = 1,
                operation = PendingOperationType.DELETE
            )
        )
    }

    @Test
    fun update_operation() = runTest {
        dao.insert(operation)
        val inserted = dao.getPendingOperations().first()
        val updated = inserted.copy(
            retryCount = 3
        )
        dao.update(updated)
        val result = dao.getPendingOperations().first()
        assertEquals(3, result.retryCount)
    }

    @Test
    fun getPendingOperations_returns_sorted_by_createdAt() = runTest {
        dao.insert(
            operation.copy(
                productId = 1,
                createdAt = 2000L
            )
        )
        dao.insert(
            operation.copy(
                productId = 2,
                createdAt = 1000L
            )
        )
        val result = dao.getPendingOperations()
        assertEquals(2, result.size)
        assertEquals(2, result[0].productId)
        assertEquals(1, result[1].productId)
    }

    @Test
    fun exists_returns_false_when_operation_not_found() = runTest {
        assertFalse(
            dao.exists(
                productId = 100,
                operation = PendingOperationType.DELETE
            )
        )
    }
}