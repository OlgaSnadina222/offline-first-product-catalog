package com.example.app_retrofit2.domain.usecase

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val productRepository: ProductRepo
) {
    suspend operator fun invoke(id: Int, product: Product): Result<Product> {
        return productRepository.updateProduct(id, product)
    }
}
