package com.example.app_retrofit2.data.remote.api

import com.example.app_retrofit2.data.remote.dto.CategoryDto
import com.example.app_retrofit2.data.remote.dto.ProductResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CategoryApi {

    @GET("products/categories")
    suspend fun getCategories(): Response<List<CategoryDto>>
}