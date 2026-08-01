package com.example.app_retrofit2.domain.usecase.favorites_screen

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetFavoriteProductsUseCase @Inject constructor(
    private val productRepository: ProductRepo
) {
    operator fun invoke(): Flow<List<Product>> {
        return productRepository.getFavoriteProducts()
    }
}