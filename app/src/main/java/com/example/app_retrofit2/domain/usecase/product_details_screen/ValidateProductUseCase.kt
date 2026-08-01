package com.example.app_retrofit2.domain.usecase.product_details_screen

import com.example.app_retrofit2.domain.model.Product
import jakarta.inject.Inject

class ValidateProductUseCase @Inject constructor() {

    operator fun invoke(product: Product): Result<Unit> {
        if (product.title.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Title cannot be empty.")
            )
        }
        if (product.brand.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Brand cannot be empty.")
            )
        }
        if (product.price <= 0.0) {
            return Result.failure(
                IllegalArgumentException("Price must be greater than 0.")
            )
        }
        if (product.stock < 0) {
            return Result.failure(
                IllegalArgumentException("Stock cannot be negative.")
            )
        }
        if (product.rating !in 0.0..5.0) {
            return Result.failure(
                IllegalArgumentException("Rating must be between 0 and 5.")
            )
        }
        if (product.description.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Description cannot be empty.")
            )
        }
        return Result.success(Unit)
    }
}