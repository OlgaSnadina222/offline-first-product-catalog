package com.example.app_retrofit2.domain.usecase

import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val productRepository: ProductRepo
) {
    suspend operator fun invoke (productId: Int) {
        productRepository.toggleFavorite(productId)
    }
}