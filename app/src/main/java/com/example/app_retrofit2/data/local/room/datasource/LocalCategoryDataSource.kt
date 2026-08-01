package com.example.app_retrofit2.data.local.room.datasource

import com.example.app_retrofit2.data.local.room.dao.CategoryDao
import com.example.app_retrofit2.data.local.room.entity.CategoryEntity
import com.example.app_retrofit2.data.local.room.mapper.toEntity
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