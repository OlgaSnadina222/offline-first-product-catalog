package com.example.app_retrofit2.data.local.datastore.repository

import com.example.app_retrofit2.data.local.datastore.datasource.UserPreferencesDataStore
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.domain.model.UserPreferences
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
class UserPreferencesRepositoryImplTest {
    @Mock
    lateinit var dataStore: UserPreferencesDataStore
    private lateinit var repository: UserPreferencesRepositoryImpl

    @Before
    fun setup() {
        repository = UserPreferencesRepositoryImpl(dataStore)
    }

    @Test
    fun preferences_delegates_to_datastore() = runTest {
        val preferences = UserPreferences(
            theme = ThemeMode.DARK,
            sort = ProductSort.PRICE_ASC,
            selectedCategory = "smartphones"
        )
        whenever(dataStore.preferences)
            .thenReturn(flowOf(preferences))

        val result = repository.preferences.first()
        assertEquals(preferences, result)
    }

    @Test
    fun saveTheme_delegates_to_datastore() = runTest {
        repository.saveTheme(ThemeMode.DARK)
        verify(dataStore).saveTheme(ThemeMode.DARK)
    }

    @Test
    fun saveSort_delegates_to_datastore() = runTest {
        repository.saveSort(ProductSort.PRICE_DESC)
        verify(dataStore).saveSort(ProductSort.PRICE_DESC)
    }
}