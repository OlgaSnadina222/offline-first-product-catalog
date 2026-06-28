package com.example.app_retrofit2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app_retrofit2.data.local.entity.ProductCategoryCrossRefEntity

@Dao
interface ProductCategoryCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<ProductCategoryCrossRefEntity>)

    @Query("DELETE FROM product_category_cross_ref")
    suspend fun clearCrossRefs()
}