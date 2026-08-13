package com.example.app_retrofit2.data.local.room.transaction

import androidx.room.withTransaction
import com.example.app_retrofit2.data.local.room.db.AppDatabase
import javax.inject.Inject

class RoomTransaction @Inject constructor(
    private val appDatabase: AppDatabase
): DatabaseTransaction {
    override suspend fun <T> withTransaction(operations: suspend () -> T) {
        return appDatabase.withTransaction {
            operations()
        }
    }
}