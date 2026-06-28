package com.example.app_retrofit2.domain.usecase

import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.repositoty.CategoryRepo
import jakarta.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepo
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<Category>> {
        return categoryRepository.getCategories(forceRefresh)
    }
}