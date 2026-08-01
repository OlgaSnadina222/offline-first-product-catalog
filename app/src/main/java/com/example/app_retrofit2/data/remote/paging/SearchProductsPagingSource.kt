package com.example.app_retrofit2.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app_retrofit2.data.local.room.mapper.toDomain
import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteProductDataSource
import com.example.app_retrofit2.data.remote.mapper.toEntity
import com.example.app_retrofit2.domain.model.Product

class SearchProductsPagingSource(
    private val remoteRepo: RemoteProductDataSource,
    private val query: String
) : PagingSource<Int, Product>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Product> {
        return try {
            val skip = params.key ?: 0
            val products = remoteRepo.searchProducts(
                query = query,
                limit = params.loadSize,
                skip = skip
            ).getOrThrow()
            LoadResult.Page(
                data = products.map { it.toEntity().toDomain() },
                prevKey = null,
                nextKey =
                    if (products.isEmpty()) null
                    else skip + params.loadSize
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? = null
}