package com.example.app_retrofit2.domain.usecase.products_screen

import com.example.app_retrofit2.domain.repositoty.ProductRepo
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class DeleteProductUseCaseTest {
    @Mock
    lateinit var repository: ProductRepo
    private lateinit var useCase: DeleteProductUseCase

    @Before
    fun setup() {
        useCase = DeleteProductUseCase(repository)
    }

    @Test
    fun invoke_returns_success_when_repository_deletes_product() = runTest {
        whenever(repository.deleteProduct(1))
            .thenReturn(Result.success(Unit))
        val result = useCase(1)
        assertTrue(result.isSuccess)
        verify(repository).deleteProduct(1)
    }

    @Test
    fun invoke_returns_failure_when_repository_fails() = runTest {
        val exception = IOException()
        whenever(repository.deleteProduct(1))
            .thenReturn(Result.failure(exception))
        val result = useCase(1)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(repository).deleteProduct(1)
    }
}