package com.example.app_retrofit2.domain.usecase

import androidx.paging.PagingData
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetPagedProductsUseCase @Inject constructor(
    private val productRepository: ProductRepo
) {
    operator fun invoke(category: String): Flow<PagingData<Product>> {
        return productRepository.getPagedProducts(
            category = category
        )
    }
}
