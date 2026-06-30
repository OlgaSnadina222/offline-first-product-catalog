package com.example.app_retrofit2.domain.repositoty

import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepo {
    val preferences: Flow<UserPreferences>

    suspend fun saveTheme(theme: ThemeMode)
    suspend fun saveSort(sort: ProductSort)
}