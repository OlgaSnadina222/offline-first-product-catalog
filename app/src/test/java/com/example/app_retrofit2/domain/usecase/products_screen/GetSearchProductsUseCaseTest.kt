package com.example.app_retrofit2.domain.usecase.products_screen

import androidx.paging.PagingData
import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetSearchProductsUseCaseTest {
    @Mock
    lateinit var repository: ProductRepo
    private lateinit var useCase: GetSearchProductsUseCase

    @Before
    fun setup() {
        useCase = GetSearchProductsUseCase(repository)
    }

    @Test
    fun invoke_calls_repository_searchProducts() = runTest {
        whenever(
            repository.searchProducts("phone")
        ).thenReturn(
            flowOf(PagingData.empty()))
        useCase("phone").first()
        verify(repository).searchProducts("phone")
    }
}