package com.example.app_retrofit2.domain.usecase.product_details_screen

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepo
) {
    suspend operator fun invoke(id: Int): Result<Product> {
        return productRepository.getProductById(id)
    }
}