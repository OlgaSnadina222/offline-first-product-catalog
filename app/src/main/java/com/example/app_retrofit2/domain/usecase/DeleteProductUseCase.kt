package com.example.app_retrofit2.domain.usecase

import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepo
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.deleteProduct(id)
    }
}