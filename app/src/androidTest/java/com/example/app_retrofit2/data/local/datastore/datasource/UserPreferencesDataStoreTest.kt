package com.example.app_retrofit2.data.local.datastore.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.model.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserPreferencesDataStoreTest {
    private lateinit var context: Context
    private lateinit var dataStore: UserPreferencesDataStore

    @Before
    fun setup() = runTest {
        context = ApplicationProvider.getApplicationContext()
        dataStore = UserPreferencesDataStore(context)
        context.dataStore.edit { it.clear() }
    }

    @Test
    fun default_preferences_are_returned() = runTest {
        val preferences = dataStore.preferences.first()
        assertEquals(ThemeMode.SYSTEM, preferences.theme)
        assertEquals(ProductSort.DEFAULT, preferences.sort)
        assertEquals("all", preferences.selectedCategory)
    }

    @Test
    fun save_theme_updates_preferences() = runTest {
        dataStore.saveTheme(ThemeMode.DARK)
        val preferences = dataStore.preferences.first()
        assertEquals(ThemeMode.DARK, preferences.theme)
    }

    @Test
    fun save_sort_updates_preferences() = runTest {
        dataStore.saveSort(ProductSort.PRICE_ASC)
        val preferences = dataStore.preferences.first()
        assertEquals(ProductSort.PRICE_ASC, preferences.sort)
    }

    @Test
    fun saving_theme_does_not_change_sort() = runTest {
        dataStore.saveTheme(ThemeMode.LIGHT)
        val preferences = dataStore.preferences.first()
        assertEquals(ThemeMode.LIGHT, preferences.theme)
        assertEquals(ProductSort.DEFAULT, preferences.sort)
    }

    @Test
    fun saving_sort_does_not_change_theme() = runTest {
        dataStore.saveSort(ProductSort.PRICE_DESC)
        val preferences = dataStore.preferences.first()
        assertEquals(ProductSort.PRICE_DESC, preferences.sort)
        assertEquals(ThemeMode.SYSTEM, preferences.theme)
    }
}