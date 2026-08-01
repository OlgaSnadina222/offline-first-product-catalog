package com.example.app_retrofit2.domain.usecase.products_screen

import androidx.paging.PagingData
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetPagedProductsUseCase @Inject constructor(
    private val productRepository: ProductRepo
) {
    operator fun invoke(category: String, sort: ProductSort): Flow<PagingData<Product>> {
        return productRepository.getPagedProducts(
            category = category,
            sort = sort
        )
    }
}