package com.example.app_retrofit2.presentation.products_screen

import androidx.paging.PagingData
import com.example.app_retrofit2.data.connectivity.ConnectivityObserver
import com.example.app_retrofit2.domain.model.ProductSort
import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.domain.model.UserPreferences
import com.example.app_retrofit2.domain.usecase.preferences.GetUserPreferencesUseCase
import com.example.app_retrofit2.domain.usecase.preferences.SaveSortUseCase
import com.example.app_retrofit2.domain.usecase.preferences.SaveThemeUseCase
import com.example.app_retrofit2.domain.usecase.products_screen.DeleteProductUseCase
import com.example.app_retrofit2.domain.usecase.products_screen.GetCategoriesUseCase
import com.example.app_retrofit2.domain.usecase.products_screen.GetPagedProductsUseCase
import com.example.app_retrofit2.domain.usecase.products_screen.GetSearchProductsUseCase
import com.example.app_retrofit2.domain.usecase.products_screen.ToggleFavoriteUseCase
import com.example.app_retrofit2.presentation.common.events.ProductsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ProductsScreenViewModelTest {
    @Mock
    lateinit var getPagedProductsUseCase: GetPagedProductsUseCase
    @Mock
    lateinit var getSearchProductsUseCase: GetSearchProductsUseCase
    @Mock
    lateinit var getCategoriesUseCase: GetCategoriesUseCase
    @Mock
    lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    @Mock
    lateinit var getUserPreferencesUseCase: GetUserPreferencesUseCase
    @Mock
    lateinit var saveThemeUseCase: SaveThemeUseCase
    @Mock
    lateinit var saveSortUseCase: SaveSortUseCase
    @Mock
    lateinit var deleteProductUseCase: DeleteProductUseCase
    @Mock
    lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var viewModel: ProductsScreenViewModel
    private val preferences = UserPreferences(
        theme = ThemeMode.SYSTEM,
        sort = ProductSort.DEFAULT
    )

    @Before
    fun setup() = runTest {
        whenever(connectivityObserver.isConnected)
            .thenReturn(MutableStateFlow(true))
        whenever(getUserPreferencesUseCase())
            .thenReturn(flowOf(preferences))
        whenever(getCategoriesUseCase(false))
            .thenReturn(Result.success(emptyList()))

        viewModel = ProductsScreenViewModel(
            getPagedProductsUseCase,
            getSearchProductsUseCase,
            getCategoriesUseCase,
            toggleFavoriteUseCase,
            getUserPreferencesUseCase,
            saveThemeUseCase,
            saveSortUseCase,
            deleteProductUseCase,
            connectivityObserver
        )
    }

    @Test
    fun onQueryChange_updates_query_and_mode() {
        viewModel.onEvent(
            ProductsScreenUiEvents.OnQueryChange("phone")
        )
        val state = viewModel.productUiState.value
        assertEquals("phone", state.filters.query)
        assertEquals(
            ProductsMode.SEARCH,
            state.filters.mode
        )
    }

    @Test
    fun empty_query_switches_to_category_mode() {
        viewModel.onEvent(
            ProductsScreenUiEvents.OnQueryChange("")
        )
        val state = viewModel.productUiState.value
        assertEquals("", state.filters.query)
        assertEquals(
            ProductsMode.CATEGORY,
            state.filters.mode
        )
    }

    @Test
    fun onCategorySelected_updates_category() {
        viewModel.onEvent(
            ProductsScreenUiEvents.OnCategorySelected("beauty")
        )
        assertEquals(
            "beauty",
            viewModel.productUiState.value.filters.category
        )
    }

    @Test
    fun onExpand_updates_expanded_state() {
        viewModel.onEvent(
            ProductsScreenUiEvents.OnExpand(true)
        )
        assertTrue(viewModel.productUiState.value.expanded)
    }

    @Test
    fun init_loads_preferences() {
        assertEquals(
            preferences,
            viewModel.productUiState.value.preferences
        )
    }

    @Test
    fun init_loads_categories() = runTest {
        advanceUntilIdle()
        assertTrue(
            viewModel.categoriesState.value is UiState.Success
        )
        verify(getCategoriesUseCase)
            .invoke(false)
    }
}