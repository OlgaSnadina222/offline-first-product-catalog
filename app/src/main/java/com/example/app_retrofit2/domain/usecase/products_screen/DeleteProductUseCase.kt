package com.example.app_retrofit2.domain.usecase.products_screen

import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepo
) {
    suspend operator fun invoke(productId: Int): Result<Unit> {
        return repository.deleteProduct(productId)
    }
}