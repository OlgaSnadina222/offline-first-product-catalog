package com.example.app_retrofit2.domain.repositoty

import com.example.app_retrofit2.domain.model.Category

interface CategoryRepo {
    suspend fun getCategories(forceRefresh: Boolean = false): Result<List<Category>>

}