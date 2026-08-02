package com.example.app_retrofit2.data.local.datastore.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        val SORT = stringPreferencesKey("sort")
        val CATEGORY = stringPreferencesKey("category")
    }
    val preferences: Flow<UserPreferences> =
        context.dataStore.data.map { preferences ->
                UserPreferences(
                    theme = preferences[PreferencesKeys.THEME]?.let {
                        runCatching { ThemeMode.valueOf(it) }
                            .getOrDefault(ThemeMode.SYSTEM)
                        } ?: ThemeMode.SYSTEM,
                    sort = preferences[PreferencesKeys.SORT]?.let {
                        runCatching { ProductSort.valueOf(it) }
                            .getOrDefault(ProductSort.DEFAULT)
                        } ?: ProductSort.DEFAULT,
                    selectedCategory = preferences[PreferencesKeys.CATEGORY] ?: "all"
                )
            }

    suspend fun saveTheme(theme: ThemeMode) {
        context.dataStore.edit {
            it[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun saveSort(sort: ProductSort) {
        context.dataStore.edit { it[PreferencesKeys.SORT] = sort.name
        }
    }
}