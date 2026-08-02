package com.example.app_retrofit2.domain.usecase.products_screen

import com.example.app_retrofit2.domain.repositoty.ProductRepo
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class ToggleFavoriteUseCaseTest {
    @Mock
    lateinit var repository: ProductRepo
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        useCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun invoke_calls_repository_toggleFavorite() = runTest {
        useCase(1)
        verify(repository).toggleFavorite(1)
    }
}