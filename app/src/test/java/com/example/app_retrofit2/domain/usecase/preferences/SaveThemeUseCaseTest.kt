package com.example.app_retrofit2.domain.usecase.preferences

import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.domain.repositoty.UserPreferencesRepo
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class SaveThemeUseCaseTest {
    @Mock
    lateinit var repository: UserPreferencesRepo
    private lateinit var useCase: SaveThemeUseCase

    @Before
    fun setup() {
        useCase = SaveThemeUseCase(repository)
    }

    @Test
    fun invoke_calls_repository_saveTheme() = runTest {
        val theme = ThemeMode.DARK
        useCase(theme)
        verify(repository).saveTheme(theme)
    }
}