package com.example.app_retrofit2.data.local.datastore.repository

import com.example.app_retrofit2.data.local.datastore.datasource.UserPreferencesDataStore
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.domain.model.UserPreferences
import com.example.app_retrofit2.domain.repositoty.UserPreferencesRepo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : UserPreferencesRepo {

    override val preferences: Flow<UserPreferences> get() = dataStore.preferences

    override suspend fun saveTheme(theme: ThemeMode) {
        dataStore.saveTheme(theme)
    }

    override suspend fun saveSort(sort: ProductSort) {
        dataStore.saveSort(sort)
    }
}