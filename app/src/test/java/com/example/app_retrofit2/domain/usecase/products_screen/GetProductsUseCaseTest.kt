package com.example.app_retrofit2.domain.usecase.products_screen

import androidx.paging.PagingData
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetProductsUseCaseTest {
    @Mock
    lateinit var repository: ProductRepo

    private lateinit var useCase: GetProductsUseCase

    @Before
    fun setup() {
        useCase = GetProductsUseCase(repository)
    }

    @Test
    fun invoke_calls_repository_with_all_category() = runTest {
        whenever(
            repository.getPagedProducts(
                category = "all",
                sort = ProductSort.PRICE_DESC
            )
        ).thenReturn(flowOf(PagingData.empty()))
        useCase(ProductSort.PRICE_DESC).first()
        verify(repository).getPagedProducts(
            category = "all",
            sort = ProductSort.PRICE_DESC
        )
    }
}