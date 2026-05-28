package com.example.app_retrofit2.data.remote.datasource.dummyjson

import com.example.app_retrofit2.data.remote.api.CategoryApi
import com.example.app_retrofit2.data.remote.dto.CategoryDto
import jakarta.inject.Inject
import okio.IOException
import retrofit2.HttpException

class RemoteCategoryDataSource @Inject constructor(
    private val categoryApi: CategoryApi
) {
    suspend fun getCategories(): Result<List<CategoryDto>> {
        return try {
            val response = categoryApi.getCategories()
            if (response.isSuccessful) {
                val body = response.body() ?: return Result.failure(Exception("Body is null"))
                Result.success(body)
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()}"))
            }
        } catch (ioException: IOException) {
            Result.failure(ioException)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}