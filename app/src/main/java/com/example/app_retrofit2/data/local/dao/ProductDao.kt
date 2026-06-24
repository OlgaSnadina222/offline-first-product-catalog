package com.example.app_retrofit2.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.app_retrofit2.data.local.entity.ProductEntity
import com.example.app_retrofit2.data.local.entity.ProductWithFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getProducts(): Flow<List<ProductEntity>>

    @Transaction
    @Query("SELECT * FROM products WHERE isVisible = 1 ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, ProductWithFavorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE id NOT IN (SELECT productId FROM favorites)")
    suspend fun clearNotFavoriteProducts()

    @Query("UPDATE products SET isVisible = 0 WHERE id IN (SELECT productId FROM favorites)")
    suspend fun hideFavoritesCashedProducts()

    @Transaction
    suspend fun clearProducts() {
        clearNotFavoriteProducts()
        hideFavoritesCashedProducts()
    }

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductsCount(): Int

}
