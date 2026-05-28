package com.example.app_retrofit2.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app_retrofit2.data.remote.api.ProductApi
import com.example.app_retrofit2.data.remote.dto.ProductDto
import com.example.app_retrofit2.data.remote.mapper.toDomain
import com.example.app_retrofit2.domain.model.Product
import retrofit2.HttpException

class SearchProductsPagingSource (
    private val productApi: ProductApi,
    private val query: String
) : PagingSource<Int, ProductDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDto> {
        return try {
            val skip = params.key ?: 0
            val limit = params.loadSize
            val response = productApi.searchProducts(
                query = query,
                limit = limit,
                skip = skip
            )
            if (response.isSuccessful) {
                val products = response.body()?.products ?: emptyList()
                LoadResult.Page(
                    data = products,
                    prevKey = if (skip == 0) null
                        else skip - limit,
                    nextKey =
                        if (products.isEmpty()) null
                        else skip + limit
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductDto>): Int? {
        return state.anchorPosition
    }
}