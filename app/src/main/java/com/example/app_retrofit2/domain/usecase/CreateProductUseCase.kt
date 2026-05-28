package com.example.app_retrofit2.domain.usecase

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject

class CreateProductUseCase @Inject constructor(
    private val productRepository: ProductRepo
) {
    suspend operator fun invoke(product: Product): Result<Product> {
        return productRepository.createProduct(product)
    }
}