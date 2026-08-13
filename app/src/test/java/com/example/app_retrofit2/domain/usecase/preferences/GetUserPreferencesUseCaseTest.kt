package com.example.app_retrofit2.domain.usecase.preferences

import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.domain.model.UserPreferences
import com.example.app_retrofit2.domain.repositoty.UserPreferencesRepo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetUserPreferencesUseCaseTest {
    @Mock
    lateinit var repository: UserPreferencesRepo
    private lateinit var useCase: GetUserPreferencesUseCase

    @Before
    fun setup() {
        useCase = GetUserPreferencesUseCase(repository)
    }

    @Test
    fun invoke_returns_preferences_flow() = runTest {
        val preferences = UserPreferences(
            theme = ThemeMode.DARK,
            sort = ProductSort.PRICE_ASC,
            selectedCategory = "beauty"
        )
        whenever(repository.preferences)
            .thenReturn(flowOf(preferences))

        val result = useCase().first()
        assertEquals(preferences, result)
        verify(repository).preferences
    }
}