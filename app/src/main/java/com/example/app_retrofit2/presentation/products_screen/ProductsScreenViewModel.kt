package com.example.app_retrofit2.presentation.products_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.app_retrofit2.domain.model.Category
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.usecase.CreateProductUseCase
import com.example.app_retrofit2.domain.usecase.DeleteProductUseCase
import com.example.app_retrofit2.domain.usecase.GetCategoriesUseCase
import com.example.app_retrofit2.domain.usecase.GetPagedProductsUseCase
import com.example.app_retrofit2.domain.usecase.GetProductsUseCase
import com.example.app_retrofit2.domain.usecase.SearchProductsUseCase
import com.example.app_retrofit2.domain.usecase.UpdateProductUseCase
import com.example.app_retrofit2.presentation.common.events.ProductsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsScreenViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    //private val searchProductsUseCase: SearchProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
): ViewModel() {
    val state = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)

    //private val _query = MutableStateFlow("")
    //val query = _query.asStateFlow()
    //val mode = MutableStateFlow<ProductsMode>(ProductsMode.All)

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()
    private val _filters = MutableStateFlow(ProductFilters())
    val filters = _filters.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val products = filters.flatMapLatest { filters ->
            getPagedProductsUseCase(
                query = filters.query,
                category = filters.category
            )
        }.cachedIn(viewModelScope)

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val products = mode.flatMapLatest { mode ->
//        when(mode) {
//            is ProductsMode.All -> getPagedProductsUseCase()
//            is ProductsMode.SearchMode -> {
//                searchProductsUseCase(mode.query)
//            }
//        }
//    }.cachedIn(viewModelScope)

    fun onEvent(event: ProductsScreenUiEvents) {
        when(event) {
            is ProductsScreenUiEvents.OnQueryChange -> {
//                mode.value = ProductsMode.SearchMode(event.query)
//                if (event.query.isEmpty()){
//                    mode.value = ProductsMode.All
//                } else {
//                    mode.value = ProductsMode.SearchMode(event.query)
//                }
//                _query.value = event.query
                _filters.value = _filters.value.copy(
                    query = event.query)
            }
            is ProductsScreenUiEvents.CreateProduct -> {
                createProduct(event.product)
            }
            is ProductsScreenUiEvents.UpdateProduct -> {
                updateProduct(event.id, event.product)
            }
            is ProductsScreenUiEvents.DeleteProduct -> {
                deleteProduct(event.id)
            }
            is ProductsScreenUiEvents.OnCategorySelected -> {
                _filters.value = _filters.value.copy(
                    category = event.category.toString()
                )
            }
        }
    }

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().onSuccess { list ->
                _categories.value = listOf(
                            Category(
                                slug = "all",
                                name = "All",
                                url = ""
                            )
                        ) + list
                }
                .onFailure {

                }
        }
    }

    private fun onQueryChange(newQuery: String) {
        //_query.value = newQuery
        _filters.value = _filters.value.copy(
            query = newQuery)
    }

    private fun createProduct(product: Product) {
        viewModelScope.launch {
            state.value = UiState.Loading
            createProductUseCase(product)
                .fold(
                onSuccess = { },
                onFailure = { error ->
                    state.value = UiState.Error.Unknown(error.message ?: "Unknown error")
                }
            )
        }
    }

    private fun updateProduct(id: Int, product: Product) {
        viewModelScope.launch {
            updateProductUseCase(id, product)
                .fold(
                onSuccess = { },
                    onFailure = { error ->
                        state.value = UiState.Error.Unknown(error.message ?: "Unknown error")
                    }
            )
        }
    }

    private fun deleteProduct(id: Int) {
        viewModelScope.launch {
            deleteProductUseCase(id).fold(
                onSuccess = {
                    val current = (state.value as? UiState.Success)?.data ?: return@launch
                    state.value = UiState.Success(current.filterNot { it.id == id })
                },
                onFailure = { error ->
                    state.value = UiState.Error.Unknown(error.message ?: "Unknown error")
                }
            )
        }
    }

    //    private fun observeSearch() {
//        viewModelScope.launch {
//            _query
//                .debounce(400)
//                .distinctUntilChanged()
//                .collectLatest { query ->
//                    if (query.isBlank()) {
//                        page = 0
//                        endReached = false
//                        loadNextPage()
//                    } else {
//                        state.value = UiState.Loading
//                        val result = searchProductsUseCase(query)
//                        result.fold(
//                            onSuccess = { list ->
//                                state.value = UiState.Success(list)
//                            },
//                            onFailure = { error ->
//                                state.value = UiState.Error.Unknown(error.message ?: "Unknown error")
//                            }
//                        )
//                    }
//                }
//        }
//    }
}

