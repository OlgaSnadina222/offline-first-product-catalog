package com.example.app_retrofit2.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app_retrofit2.data.local.room.entity.ProductCategoryCrossRef

@Dao
interface ProductCategoryCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<ProductCategoryCrossRef>)

    @Query("DELETE FROM product_category_cross_ref WHERE categorySlug = :category")
    suspend fun clearCrossRefs(category: String)

    @Query("SELECT * FROM product_category_cross_ref")
    suspend fun getAllCrossRefs(): List<ProductCategoryCrossRef>
}