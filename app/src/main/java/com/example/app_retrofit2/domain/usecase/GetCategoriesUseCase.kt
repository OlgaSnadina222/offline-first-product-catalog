package com.example.app_retrofit2.domain.usecase

import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.repositoty.CategoryRepo
import jakarta.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepo
) {
    suspend operator fun invoke(): Result<List<Category>> {
        return repository.getCategories()
    }
}