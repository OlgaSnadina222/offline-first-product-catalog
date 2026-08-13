package com.example.app_retrofit2.di

import androidx.room.RoomDatabase
import com.example.app_retrofit2.data.local.room.transaction.DatabaseTransaction
import com.example.app_retrofit2.data.local.room.transaction.RoomTransaction
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
    abstract class RoomTransactionModule {
        @Binds
        abstract fun bindRoomTransaction (impl: RoomTransaction): DatabaseTransaction
    }