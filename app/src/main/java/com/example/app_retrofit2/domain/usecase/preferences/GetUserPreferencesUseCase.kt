package com.example.app_retrofit2.domain.usecase.preferences

import com.example.app_retrofit2.domain.model.UserPreferences
import com.example.app_retrofit2.domain.repositoty.UserPreferencesRepo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserPreferencesUseCase @Inject constructor(
    private val repository: UserPreferencesRepo
) {

    operator fun invoke(): Flow<UserPreferences> {
        return repository.preferences
    }
}