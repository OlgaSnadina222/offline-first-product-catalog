package com.example.app_retrofit2.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app_retrofit2.data.sync.PendingOperationType
import com.example.app_retrofit2.data.sync.SyncStatus

@Entity("pending_operations")
data class PendingOperationEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Int,
    val operation: PendingOperationType,
    val retryCount: Int = 0,
    val createdAt: Long,
    val status: SyncStatus = SyncStatus.PENDING
)