package com.example.app_retrofit2.data.local.datasource.room

import com.example.app_retrofit2.data.local.dao.CategoryDao
import com.example.app_retrofit2.data.local.entity.CategoryEntity
import com.example.app_retrofit2.data.local.mapper.toEntity
import com.example.app_retrofit2.domain.model.Category
import javax.inject.Inject

class LocalCategoryDataSource @Inject constructor(
    private val categoryDao: CategoryDao
){
    suspend fun getCategories(): List<CategoryEntity> {
        return categoryDao.getCategories()
    }

    suspend fun insertCategories(categories: List<Category>) {
        categoryDao.insertCategories(
            categories.map { category ->
                category.toEntity()
            }
        )
    }

    suspend fun clearCategories() {
        categoryDao.clearCategories()
    }
}