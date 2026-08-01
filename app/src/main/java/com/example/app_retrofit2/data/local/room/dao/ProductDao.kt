package com.example.app_retrofit2.data.local.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.local.room.entity.ProductWithCategories
import com.example.app_retrofit2.data.local.room.entity.ProductWithFavorite
import com.example.app_retrofit2.data.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
    interface ProductDao {
        @Query("SELECT * FROM products")
        fun getProducts(): Flow<List<ProductEntity>>

        @Transaction
        @Query("SELECT * FROM products WHERE isVisible = 1 AND isDeleted = 0 AND (:category = 'all' OR category = :category) ORDER BY id ASC")
        fun pagingSource(category: String): PagingSource<Int, ProductWithFavorite>

        @Transaction
        @Query("SELECT * FROM products WHERE isVisible = 1 AND isDeleted = 0 AND (:category = 'all' OR category = :category) ORDER BY price ASC")
        fun pagingSourcePriceAsc(category: String): PagingSource<Int, ProductWithFavorite>

        @Transaction
        @Query("SELECT * FROM products WHERE isVisible = 1 AND isDeleted = 0 AND (:category = 'all' OR category = :category) ORDER BY price DESC")
        fun pagingSourcePriceDesc(category: String): PagingSource<Int, ProductWithFavorite>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertProducts(products: List<ProductEntity>)

        @Query("DELETE FROM products WHERE id NOT IN (SELECT productId FROM favorites) AND category = :category")
        suspend fun clearNotFavoriteProducts(category: String)

        @Query("UPDATE products SET isVisible = 0 WHERE id IN (SELECT productId FROM favorites)")
        suspend fun hideFavoritesCashedProducts()

        @Transaction
    suspend fun clearProducts(category: String) {
        clearNotFavoriteProducts(category)
        hideFavoritesCashedProducts()
    }

    @Transaction
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductWithFavorite?

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductsCount(): Int

    @Transaction
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductWithCategories(id: Int): ProductWithCategories?

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Transaction
    @Query("SELECT * FROM products WHERE id = :id")
    fun observeProductById(id: Int): Flow<ProductWithFavorite?>

    @Query("UPDATE products SET isDeleted = 1, syncStatus = :status WHERE id = :id")
    suspend fun softDelete(id: Int, status: SyncStatus = SyncStatus.PENDING)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun hardDelete(id: Int)

    @Query("SELECT MIN(updatedAt) FROM products WHERE isDeleted = 0 AND isVisible = 1 AND (:category = 'all' OR category = :category)")
    suspend fun getOldestUpdatedAt(category: String): Long?

    @Query("UPDATE products SET updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateUpdatedAt(id: Int, updatedAt: Long)

    @Query("UPDATE products SET updatedAt = :updatedAt WHERE (:category = 'all' OR category = :category)")
    suspend fun updateCategoryUpdatedAt(category: String, updatedAt: Long)

}
