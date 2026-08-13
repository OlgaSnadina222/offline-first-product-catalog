package com.example.app_retrofit2.data.local.room.transaction

interface DatabaseTransaction {

    suspend fun <T> withTransaction(
       operations: suspend () -> T
    )
}