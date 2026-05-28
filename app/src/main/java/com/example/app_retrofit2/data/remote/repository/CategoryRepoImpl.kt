package com.example.app_retrofit2.data.remote.repository

import com.example.app_retrofit2.data.remote.datasource.dummyjson.RemoteCategoryDataSource
import com.example.app_retrofit2.data.remote.mapper.toDomain
import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.repositoty.CategoryRepo
import jakarta.inject.Inject

class CategoryRepoImpl @Inject constructor(
    private val remoteCategoryDataSource: RemoteCategoryDataSource
) : CategoryRepo {
    override suspend fun getCategories(): Result<List<Category>> {
        return remoteCategoryDataSource.getCategories().map { list ->
            list.map { it.toDomain() }
        }
    }

}