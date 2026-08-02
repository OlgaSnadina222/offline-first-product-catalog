package com.example.app_retrofit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app_retrofit2.data.local.room.entity.PendingOperationEntity
import com.example.app_retrofit2.data.sync.PendingOperationType

@Dao
interface PendingOperationDao {

    @Query("SELECT * FROM pending_operations ORDER BY createdAt")
    suspend fun getPendingOperations(): List<PendingOperationEntity>
    @Insert
    suspend fun insert(operation: PendingOperationEntity)
    @Delete
    suspend fun delete(operation: PendingOperationEntity)
    @Update
    suspend fun update(operation: PendingOperationEntity)
    @Query("SELECT EXISTS(SELECT 1 FROM pending_operations WHERE productId = :productId AND operation = :operation)")
    suspend fun exists(productId: Int, operation: PendingOperationType): Boolean
}