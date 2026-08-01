package com.example.app_retrofit2.domain.usecase.product_details_screen

import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveProductByIdUseCase @Inject constructor(
    private val repository: ProductRepo
) {
    operator fun invoke(id: Int): Flow<Product> {
        return repository.observeProductById(id)
    }
}