package com.example.app_retrofit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.app_retrofit2.data.local.room.entity.FavoriteEntity
import com.example.app_retrofit2.data.local.room.entity.FavoriteWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :productId")
    suspend fun removeFavorite(productId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :productId)")
    suspend fun exists(productId: Int): Boolean

    @Transaction
    @Query("SELECT * FROM favorites")
    fun getFavoriteProducts(): Flow<List<FavoriteWithProduct>>

}