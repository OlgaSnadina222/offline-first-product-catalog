package com.example.app_retrofit2.presentation.product_details_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.UserPreferences
import com.example.app_retrofit2.domain.usecase.product_details_screen.GetProductByIdUseCase
import com.example.app_retrofit2.domain.usecase.preferences.GetUserPreferencesUseCase
import com.example.app_retrofit2.domain.usecase.product_details_screen.ObserveProductByIdUseCase
import com.example.app_retrofit2.domain.usecase.product_details_screen.UpdateProductUseCase
import com.example.app_retrofit2.domain.usecase.product_details_screen.ValidateProductUseCase
import com.example.app_retrofit2.presentation.common.events.ProductDetailsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsScreenViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val validateProductUseCase: ValidateProductUseCase,
    private val observeProductByIdUseCase: ObserveProductByIdUseCase

): ViewModel() {
    private val _state = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val state: StateFlow<UiState<Product>> = _state.asStateFlow()
    private val _editableProduct = MutableStateFlow<Product?>(null)
    val editableProduct = _editableProduct.asStateFlow()
    val preferences = getUserPreferencesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences()
    )
    val _saveProductSuccess = MutableSharedFlow<Unit>()
    val saveProductSuccess = _saveProductSuccess.asSharedFlow()
    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()


    fun onEvent(event: ProductDetailsScreenUiEvents) {
        when(event) {
            is ProductDetailsScreenUiEvents.GetProductById ->
                observeProduct(event.id)
            is ProductDetailsScreenUiEvents.OnProductChanged ->
                _editableProduct.value = event.product
            ProductDetailsScreenUiEvents.OnSaveChanges ->
                saveChanges()
            ProductDetailsScreenUiEvents.OnStartEditing -> {
                _isEditing.value = true
            }
            ProductDetailsScreenUiEvents.OnStopEditing -> {
                _isEditing.value = false
            }
        }
    }

    private fun getProductById(id: Int) {
        _state.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getProductByIdUseCase(id)
                result.fold(
                    onSuccess = { product ->
                        _state.value = UiState.Success(product)
                        _editableProduct.value = product
                    },
                    onFailure = { error ->
                        _state.value = UiState.Error.Unknown(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _state.value = UiState.Error.Unknown(e.message ?: "Unknown error")
            }
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            val product = _editableProduct.value ?: return@launch
            validateProductUseCase(product)
                .onFailure { error ->
                    _state.value = UiState.Error.Unknown(
                        error.message ?: "Validation failed"
                    )
                    return@launch
                }
            updateProductUseCase(product)
                .onSuccess {
                    _isEditing.value = false
                    _saveProductSuccess.emit(Unit)
                }
                .onFailure { error ->
                    _state.value = UiState.Error.Unknown(
                        error.message ?: "Failed to save product"
                    )
                }
        }
    }

    private fun observeProduct(id: Int) {
        viewModelScope.launch {
            observeProductByIdUseCase(id).collect { product ->
                _state.value = UiState.Success(product)
                if (!_isEditing.value) {
                    _editableProduct.value = product
                }
            }
        }
    }
}