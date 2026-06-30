package com.example.app_retrofit2.domain.usecase.preferences

import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.domain.repositoty.UserPreferencesRepo
import jakarta.inject.Inject

class SaveThemeUseCase @Inject constructor(
    private val repository: UserPreferencesRepo
) {

    suspend operator fun invoke(theme: ThemeMode) {
        repository.saveTheme(theme)
    }
}