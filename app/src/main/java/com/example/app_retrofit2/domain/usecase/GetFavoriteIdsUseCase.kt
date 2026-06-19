package com.example.app_retrofit2.domain.usecase

import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetFavoriteIdsUseCase @Inject constructor(
    private val productRepository: ProductRepo
) {
    operator fun invoke(): Flow<List<Int>> {
        return productRepository.getFavoriteIds()
    }
}