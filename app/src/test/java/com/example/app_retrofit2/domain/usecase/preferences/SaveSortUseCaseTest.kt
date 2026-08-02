package com.example.app_retrofit2.domain.usecase.preferences

import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.repositoty.UserPreferencesRepo
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class SaveSortUseCaseTest {
    @Mock
    lateinit var repository: UserPreferencesRepo
    private lateinit var useCase: SaveSortUseCase

    @Before
    fun setup() {
        useCase = SaveSortUseCase(repository)
    }

    @Test
    fun invoke_calls_repository_saveSort() = runTest {
        val sort = ProductSort.PRICE_ASC
        useCase(sort)
        verify(repository).saveSort(sort)
    }
}