package com.example.app_retrofit2.presentation.products_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.UserPreferences
import com.example.app_retrofit2.domain.usecase.GetCategoriesUseCase
import com.example.app_retrofit2.domain.usecase.GetPagedProductsUseCase
import com.example.app_retrofit2.domain.usecase.GetSearchProductsUseCase
import com.example.app_retrofit2.domain.usecase.ToggleFavoriteUseCase
import com.example.app_retrofit2.domain.usecase.preferences.GetUserPreferencesUseCase
import com.example.app_retrofit2.domain.usecase.preferences.SaveSortUseCase
import com.example.app_retrofit2.domain.usecase.preferences.SaveThemeUseCase
import com.example.app_retrofit2.presentation.common.events.ProductsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    private val saveSortUseCase: SaveSortUseCase
): ViewModel() {
    val state = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState = _categoriesState.asStateFlow()
    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory = _selectedCategory.asStateFlow()
    private val _filters = MutableStateFlow(ProductFilters())
    val filters = _filters.asStateFlow()
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()
    private val mode = MutableStateFlow<ProductsMode>(ProductsMode.All("all"))
    val preferences = getUserPreferencesUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserPreferences()
        )
    private val sort = preferences.map { it.sort }.distinctUntilChanged()


//    @OptIn(ExperimentalCoroutinesApi::class)
//    val pagingProducts = mode.flatMapLatest { mode ->
//        when(mode) {
//            is ProductsMode.All -> getPagedProductsUseCase(
//                category = mode.category,
//                sort =
//            )
//            is ProductsMode.SearchMode -> {
//                getSearchProductsUseCase(
//                    mode.query
//                )
//            }
//        }
//    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingProducts = combine(
        mode,
        sort
    ) { mode, sort -> mode to sort
    }.flatMapLatest { (mode, sort) ->
        when (mode) {
            is ProductsMode.All -> getPagedProductsUseCase(
                    category = mode.category,
                    sort = sort
                )
            is ProductsMode.SearchMode -> getSearchProductsUseCase(mode.query)
        }
    }.cachedIn(viewModelScope)

    fun onEvent(event: ProductsScreenUiEvents) {
        when(event) {
            is ProductsScreenUiEvents.OnQueryChange -> {
                _filters.value = _filters.value.copy(
                    query = event.query)
                _query.value = event.query
                if (event.query.isBlank()) {
                    mode.value = ProductsMode.All(
                        _filters.value.category
                    )
                } else {
                    mode.value = ProductsMode.SearchMode(
                        event.query
                    )
                }
            }
            is ProductsScreenUiEvents.OnCategorySelected -> {
                _selectedCategory.value = event.categorySlug
                _filters.value = _filters.value.copy(
                    category = event.categorySlug
                )
                if (_query.value.isBlank()) {
                    mode.value = ProductsMode.All(event.categorySlug)
                }
            }
            is ProductsScreenUiEvents.ToggleFavorite -> {
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
        }
    }

    init {
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
}

