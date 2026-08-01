package com.example.app_retrofit2.domain.usecase.product_details_screen

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val repository: ProductRepo
) {
    suspend operator fun invoke(product: Product): Result<Product> {
        return repository.updateProduct(product)
    }
}