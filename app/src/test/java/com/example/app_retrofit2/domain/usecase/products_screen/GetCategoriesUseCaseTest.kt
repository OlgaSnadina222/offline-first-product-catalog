package com.example.app_retrofit2.domain.usecase.products_screen

import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.repositoty.CategoryRepo
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
class GetCategoriesUseCaseTest {
    @Mock
    lateinit var repository: CategoryRepo
    private lateinit var useCase: GetCategoriesUseCase
    private val categories = listOf(
        Category(
            slug = "beauty",
            name = "Beauty"
        ),
        Category(
            slug = "electronics",
            name = "Electronics"
        )
    )

    @Before
    fun setup() {
        useCase = GetCategoriesUseCase(repository)
    }

    @Test
    fun invoke_returns_categories_when_repository_succeeds() = runTest {
        whenever(repository.getCategories(false))
            .thenReturn(Result.success(categories))
        val result = useCase()
        assertTrue(result.isSuccess)
        assertEquals(categories, result.getOrNull())
        verify(repository).getCategories(false)
    }

    @Test
    fun invoke_passes_forceRefresh_parameter() = runTest {
        whenever(repository.getCategories(true))
            .thenReturn(Result.success(categories))
        val result = useCase(forceRefresh = true)
        assertTrue(result.isSuccess)
        assertEquals(categories, result.getOrNull())
        verify(repository).getCategories(true)
    }

    @Test
    fun invoke_returns_failure_when_repository_fails() = runTest {
        val exception = IOException()
        whenever(repository.getCategories(false))
            .thenReturn(Result.failure(exception))
        val result = useCase()
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(repository).getCategories(false)
    }
}