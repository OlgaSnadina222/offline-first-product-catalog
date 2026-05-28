package com.example.app_retrofit2.domain.usecase

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepo
) {
    suspend operator fun invoke(limit: Int, skip: Int): Result<List<Product>> {
        return productRepository.getAllProducts(limit, skip)
    }
}
