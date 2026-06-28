package com.example.app_retrofit2.presentation.product_details_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.usecase.GetProductByIdUseCase
import com.example.app_retrofit2.presentation.common.events.ProductDetailsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsScreenViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase
): ViewModel() {
    private val _state = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val state: StateFlow<UiState<Product>> = _state.asStateFlow()


    fun onEvent(event: ProductDetailsScreenUiEvents) {
        when(event) {
            is ProductDetailsScreenUiEvents.GetProductById ->
                getProductById(event.id)
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
}