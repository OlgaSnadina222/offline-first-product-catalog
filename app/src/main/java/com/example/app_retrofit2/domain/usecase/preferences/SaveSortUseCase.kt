package com.example.app_retrofit2.domain.usecase.preferences

import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.repositoty.UserPreferencesRepo
import jakarta.inject.Inject

class SaveSortUseCase @Inject constructor(
    private val repository: UserPreferencesRepo
) {

    suspend operator fun invoke(sort: ProductSort) {
        repository.saveSort(sort)
    }
}