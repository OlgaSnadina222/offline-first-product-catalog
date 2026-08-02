package com.example.app_retrofit2.data.local.room.datasource

import androidx.paging.PagingSource
import com.example.app_retrofit2.data.cache.CachePolicy
import com.example.app_retrofit2.data.local.room.dao.CategoryDao
import com.example.app_retrofit2.data.local.room.dao.FavoriteDao
import com.example.app_retrofit2.data.local.room.dao.ProductCategoryCrossRefDao
import com.example.app_retrofit2.data.local.room.dao.ProductDao
import com.example.app_retrofit2.data.local.room.dao.RemoteKeyDao
import com.example.app_retrofit2.data.local.room.entity.FavoriteEntity
import com.example.app_retrofit2.data.local.room.entity.FavoriteWithProduct
import com.example.app_retrofit2.data.local.room.entity.ProductCategoryCrossRef
import com.example.app_retrofit2.data.local.room.entity.ProductEntity
import com.example.app_retrofit2.data.local.room.entity.ProductWithFavorite
import com.example.app_retrofit2.data.local.room.entity.RemoteKeyEntity
import com.example.app_retrofit2.data.sync.SyncStatus
import com.example.app_retrofit2.domain.model.ProductSort
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class LocalProductDataSourceTest {
    @Mock
    lateinit var productDao: ProductDao
    @Mock lateinit var favoriteDao: FavoriteDao
    @Mock lateinit var remoteKeyDao: RemoteKeyDao
    @Mock lateinit var categoryDao: CategoryDao
    @Mock lateinit var crossRefDao: ProductCategoryCrossRefDao
    private lateinit var dataSource: LocalProductDataSource
    private val testProduct = ProductEntity(
        id = 1,
        title = "Mascara Waterproof",
        description = "Perfect every day mascara for beauty eyelashes.",
        category = "beauty",
        price = 15.50f,
        discountPercentage = 0f,
        rating = 4.9f,
        stock = 78,
        brand = "MaxMara",
        images = emptyList(),
        isVisible = true,
        isDeleted = false,
        syncStatus = SyncStatus.PENDING,
        updatedAt = 3000L
    )

    @Before
    fun setup() {
        dataSource = LocalProductDataSource(
            productDao,
            favoriteDao,
            remoteKeyDao,
            categoryDao,
            crossRefDao
        )
    }

    @Test
    fun getProducts_returns_products() = runTest {
        val products = listOf(testProduct)
        whenever(productDao.getProducts())
            .thenReturn(flowOf(products))
        val result = dataSource.getProducts().first()
        assertEquals(products, result)
        verify(productDao).getProducts()
    }

    @Test
    fun pagingSource_default_returns_default_paging_source() {
        val pagingSource = mock<PagingSource<Int, ProductWithFavorite>>()
        whenever(productDao.pagingSource("all")
        ).thenReturn(pagingSource)
        val result = dataSource.pagingSource(
            category = "all",
            sort = ProductSort.DEFAULT
        )
        assertSame(pagingSource, result)
        verify(productDao).pagingSource("all")
        verify(productDao, never()).pagingSourcePriceAsc(any())
        verify(productDao, never()).pagingSourcePriceDesc(any())
    }

    @Test
    fun pagingSource_priceAsc_returns_priceAsc_paging_source() {
        val pagingSource = mock<PagingSource<Int, ProductWithFavorite>>()
        whenever(productDao.pagingSourcePriceAsc("beauty")
        ).thenReturn(pagingSource)
        val result = dataSource.pagingSource(
            category = "beauty",
            sort = ProductSort.PRICE_ASC
        )
        assertSame(pagingSource, result)
        verify(productDao).pagingSourcePriceAsc("beauty")
        verify(productDao, never()).pagingSource(any())
        verify(productDao, never()).pagingSourcePriceDesc(any())
    }

    @Test
    fun pagingSource_priceDesc_returns_priceDesc_paging_source() {
        val pagingSource = mock<PagingSource<Int, ProductWithFavorite>>()
        whenever(productDao.pagingSourcePriceDesc("beauty")
        ).thenReturn(pagingSource)
        val result = dataSource.pagingSource(
            category = "beauty",
            sort = ProductSort.PRICE_DESC
        )
        assertSame(pagingSource, result)
        verify(productDao).pagingSourcePriceDesc("beauty")
        verify(productDao, never()).pagingSource(any())
        verify(productDao, never()).pagingSourcePriceAsc(any())
    }

    @Test
    fun getProductById_returns_product() = runTest {
        val productWithFavorite = ProductWithFavorite(
            product = testProduct,
            favorite = null
        )
        whenever(productDao.getProductById(1)
        ).thenReturn(productWithFavorite)
        val result = dataSource.getProductById(1)
        assertNotNull(result)
        assertEquals(testProduct, result)
        verify(productDao).getProductById(1)
    }

    @Test
    fun getProductById_returns_null_when_product_not_found() = runTest {
        whenever(productDao.getProductById(1)
        ).thenReturn(null)
        val result = dataSource.getProductById(1)
        assertNull(result)
    }

    @Test
    fun clearProducts_calls_productDao() = runTest {
        dataSource.clearProducts("beauty")
        verify(productDao).clearProducts("beauty")
    }

    @Test
    fun insertProducts_calls_productDao() = runTest {
        val products = listOf(
            testProduct,
            testProduct.copy(id = 2)
        )
        dataSource.insertProducts(products)
        verify(productDao).insertProducts(products)
    }

    @Test
    fun getProductsCount_returns_count() = runTest {
        whenever(productDao.getProductsCount()
        ).thenReturn(5)
        val result = dataSource.getProductsCount()
        assertEquals(5, result)
        verify(productDao).getProductsCount()
    }

    @Test
    fun addFavorite_calls_favoriteDao() = runTest {
        dataSource.addFavorite(1)
        verify(favoriteDao).addFavorite(FavoriteEntity(1))
    }

    @Test
    fun removeFavorite_calls_favoriteDao() = runTest {
        dataSource.removeFavorite(1)
        verify(favoriteDao).removeFavorite(1)
    }

    @Test
    fun isFavorite_returns_true_when_product_is_favorite() = runTest {
        whenever(favoriteDao.exists(1)
        ).thenReturn(true)
        val result = dataSource.isFavorite(1)
        assertTrue(result)
        verify(favoriteDao).exists(1)
    }

    @Test
    fun isFavorite_returns_false_when_product_is_not_favorite() = runTest {
        whenever(favoriteDao.exists(1)
        ).thenReturn(false)
        val result = dataSource.isFavorite(1)
        assertFalse(result)
        verify(favoriteDao).exists(1)
    }

    @Test
    fun getFavoriteProducts_returns_flow_from_dao() = runTest {
        val favorites = listOf<FavoriteWithProduct>()
        whenever(favoriteDao.getFavoriteProducts()
        ).thenReturn(flowOf(favorites))
        val result = dataSource.getFavoriteProducts().first()
        assertEquals(favorites, result)
        verify(favoriteDao).getFavoriteProducts()
    }

    @Test
    fun getRemoteKey_returns_remote_key() = runTest {
        val remoteKey = RemoteKeyEntity(
                id = "beauty",
                nextKey = 20
        )
        whenever(remoteKeyDao.getRemoteKey("beauty")
        ).thenReturn(remoteKey)
        val result = dataSource.getRemoteKey("beauty")
        assertEquals(remoteKey, result)
        verify(remoteKeyDao).getRemoteKey("beauty")
    }

    @Test
    fun insertRemoteKey_creates_remote_key_and_calls_dao() = runTest {
        dataSource.insertRemoteKey(
            nextKey = 20,
            category = "beauty"
        )
        verify(remoteKeyDao).insertRemoteKey(
            RemoteKeyEntity(
                id = "beauty",
                nextKey = 20
            )
        )
    }

    @Test
    fun insertRemoteKey_accepts_null_next_key() = runTest {
        dataSource.insertRemoteKey(
            nextKey = null,
            category = "beauty"
        )
        verify(remoteKeyDao).insertRemoteKey(
            RemoteKeyEntity(
                id = "beauty",
                nextKey = null
            )
        )
    }

    @Test
    fun clearRemoteKeys_calls_dao() = runTest {
        dataSource.clearRemoteKeys("beauty")
        verify(remoteKeyDao).clearRemoteKeys("beauty")
    }

    @Test
    fun insertCrossRefs_calls_dao() = runTest {
        val refs = listOf(
            ProductCategoryCrossRef(
                productId = 1,
                categorySlug = "beauty"
            ),
            ProductCategoryCrossRef(
                productId = 2,
                categorySlug = "beauty"
            )
        )
        dataSource.insertCrossRefs(refs)
        verify(crossRefDao).insertCrossRefs(refs)
    }

    @Test
    fun clearCrossRefs_calls_dao() = runTest {
        dataSource.clearCrossRefs("beauty")
        verify(crossRefDao).clearCrossRefs("beauty")
    }

    @Test
    fun updateProduct_calls_productDao() = runTest {
        dataSource.updateProduct(testProduct)
        verify(productDao).updateProduct(testProduct)
    }

    @Test
    fun observeProductById_maps_product_and_sets_isFavorite_true() = runTest {
        val productWithFavorite = ProductWithFavorite(
            product = testProduct,
            favorite = FavoriteEntity(testProduct.id)
        )
        whenever(productDao.observeProductById(testProduct.id)
        ).thenReturn(flowOf(productWithFavorite))
        val result = dataSource.observeProductById(testProduct.id).first()
        assertEquals(testProduct.id, result.id)
        assertTrue(result.isFavorite == true)
        verify(productDao).observeProductById(testProduct.id)
    }

    @Test
    fun observeProductById_maps_product_and_sets_isFavorite_false() = runTest {
        val productWithFavorite = ProductWithFavorite(
            product = testProduct,
            favorite = null
        )
        whenever(productDao.observeProductById(testProduct.id)
        ).thenReturn(flowOf(productWithFavorite))
        val result = dataSource.observeProductById(testProduct.id).first()
        assertFalse(result.isFavorite == true)
    }

    @Test
    fun softDeleteProduct_calls_productDao() = runTest {
        dataSource.softDeleteProduct(1)
        verify(productDao).softDelete(1)
    }

    @Test
    fun hardDeleteProduct_calls_productDao() = runTest {
        dataSource.hardDeleteProduct(1)
        verify(productDao).hardDelete(1)
    }

    @Test
    fun isCacheExpired_returns_true_when_cache_is_empty() = runTest {
        whenever(productDao.getOldestUpdatedAt("beauty")
        ).thenReturn(null)
        val result = dataSource.isCacheExpired("beauty")
        assertTrue(result)
        verify(productDao).getOldestUpdatedAt("beauty")
    }

    @Test
    fun isCacheExpired_returns_true_when_cache_is_expired() = runTest {
        val expiredTime = System.currentTimeMillis() - CachePolicy.DEFAULT_TIMEOUT - 1000
        whenever(productDao.getOldestUpdatedAt("beauty")
        ).thenReturn(expiredTime)
        val result = dataSource.isCacheExpired("beauty")
        assertTrue(result)
    }

    @Test
    fun isCacheExpired_returns_false_when_cache_is_fresh() = runTest {
        val freshTime = System.currentTimeMillis()
        whenever(productDao.getOldestUpdatedAt("beauty")
        ).thenReturn(freshTime)
        val result = dataSource.isCacheExpired("beauty")
        assertFalse(result)
    }

    @Test
    fun updateUpdatedAt_calls_productDao() = runTest {
        dataSource.updateUpdatedAt(
            productId = 1,
            updatedAt = 12345L
        )
        verify(productDao).updateUpdatedAt(
                id = 1,
                updatedAt = 12345L
            )
    }

    @Test
    fun updateCategoryUpdatedAt_calls_productDao() = runTest {
        dataSource.updateCategoryUpdatedAt(
            category = "beauty",
            updatedAt = 55555L
        )
        verify(productDao).updateCategoryUpdatedAt(
                category = "beauty",
                updatedAt = 55555L
            )
    }

}