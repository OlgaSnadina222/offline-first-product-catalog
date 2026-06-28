package com.example.app_retrofit2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.app_retrofit2.data.local.entity.CategoryEntity
import com.example.app_retrofit2.data.local.entity.CategoryWithProducts
import com.example.app_retrofit2.domain.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name")
    suspend fun getCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Transaction
    @Query("SELECT * FROM categories WHERE slug = :slug")
    suspend fun getCategoryWithProducts(slug: String): CategoryWithProducts

}