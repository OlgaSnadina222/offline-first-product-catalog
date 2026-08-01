package com.example.app_retrofit2.data.remote.api

import com.example.app_retrofit2.data.remote.dto.ProductDto
import com.example.app_retrofit2.data.remote.dto.ProductRequestDto
import com.example.app_retrofit2.data.remote.dto.ProductResponseDto
import com.example.app_retrofit2.data.remote.dto.UpdateProductDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("products")
    suspend fun getAllProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): Response<ProductResponseDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductDto>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): Response<ProductResponseDto>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category")
        category: String,
        @Query("limit")
        limit: Int,
        @Query("skip")
        skip: Int
    ): Response<ProductResponseDto>

    @PATCH("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body body: UpdateProductDto
    ): Response<ProductDto>

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<Unit>

}