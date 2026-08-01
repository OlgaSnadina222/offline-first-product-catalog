package com.example.app_retrofit2.presentation.products_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.app_retrofit2.data.connectivity.ConnectivityObserver
import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.usecase.products_screen.GetCategoriesUseCase
import com.example.app_retrofit2.domain.usecase.products_screen.GetPagedProductsUseCase
import com.example.app_retrofit2.domain.usecase.products_screen.GetSearchProductsUseCase
import com.example.app_retrofit2.domain.usecase.products_screen.ToggleFavoriteUseCase
import com.example.app_retrofit2.domain.usecase.preferences.GetUserPreferencesUseCase
import com.example.app_retrofit2.domain.usecase.preferences.SaveSortUseCase
import com.example.app_retrofit2.domain.usecase.preferences.SaveThemeUseCase
import com.example.app_retrofit2.domain.usecase.products_screen.DeleteProductUseCase
import com.example.app_retrofit2.presentation.common.events.ProductsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsScreenViewModel @Inject constructor(
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val getSearchProductsUseCase: GetSearchProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    private val saveSortUseCase: SaveSortUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {
    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState = _categoriesState.asStateFlow()

    private val _productUiState = MutableStateFlow(ProductsUiState())
    val productUiState = _productUiState.asStateFlow()
    val isConnected = connectivityObserver.isConnected


    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingProducts = productUiState.flatMapLatest { productUiState ->
        when (productUiState.filters.mode) {
            ProductsMode.CATEGORY -> getPagedProductsUseCase(
                    category = productUiState.filters.category,
                    sort = productUiState.preferences.sort
                )
            ProductsMode.SEARCH -> getSearchProductsUseCase(productUiState.filters.query)
        }
    }.cachedIn(viewModelScope)

    fun onEvent(event: ProductsScreenUiEvents) {
        when(event) {
            is ProductsScreenUiEvents.OnQueryChange -> {
                _productUiState.update { uiState ->
                    uiState.copy(
                        filters = uiState.filters.copy(
                            query = event.query,
                            mode = if (event.query.isBlank()){
                                ProductsMode.CATEGORY
                            } else {
                                ProductsMode.SEARCH
                            }
                        )
                    )
                }
            }
            is ProductsScreenUiEvents.OnCategorySelected -> {
                _productUiState.update { uiState ->
                    uiState.copy(
                        filters = uiState.filters.copy(
                            category = event.categorySlug,
                            mode = if (uiState.filters.query.isBlank()){
                                ProductsMode.CATEGORY
                            } else {
                                ProductsMode.SEARCH
                            }
                        )
                    )
                }
            }
            is ProductsScreenUiEvents.OnToggleFavorite -> {
                toggleFavorite(event.productId)
            }
            is ProductsScreenUiEvents.OnThemeSelected -> {
                viewModelScope.launch {
                    saveThemeUseCase(event.theme)
                }
            }
            is ProductsScreenUiEvents.OnSortSelected -> {
                viewModelScope.launch {
                    saveSortUseCase(event.sort)
                }
            }
            is ProductsScreenUiEvents.OnExpand -> {
                _productUiState.update { uiState ->
                    uiState.copy(
                        expanded = event.expanded
                    )
                }
            }
            is ProductsScreenUiEvents.OnDeleteProduct -> {
                deleteProduct(event.productId)
            }
        }
    }

    init {
        getUserPreferencesUseCase().onEach { preferences ->
            _productUiState.update { uiState ->
                uiState.copy(
                    preferences = preferences
                )
            }
        }.launchIn(viewModelScope)
        loadCategories()
    }

    private fun loadCategories( forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            getCategoriesUseCase(forceRefresh)
                .onSuccess { categories ->
                    _categoriesState.value = UiState.Success(
                            listOf(
                                Category(
                                    slug = "all",
                                    name = "All"
                                )
                            ) + categories
                        )
                }
                .onFailure { error ->
                    _categoriesState.value = UiState.Error.Unknown(error.message ?: "Unknown error")
                }
        }
    }

    private fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(productId)
        }
    }

    private fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            deleteProductUseCase(productId)
                .onSuccess {
                    Log.e("ProductsScreen", "$productId - Delete success")
                }
                .onFailure {
                    Log.e("ProductsScreen", "$productId - Delete failed")
            }
        }
    }
}

