package com.example.app_retrofit2.presentation.common.events

import com.example.app_retrofit2.domain.model.Product

sealed class ProductDetailsScreenUiEvents {
    data class GetProductById(val id: Int): ProductDetailsScreenUiEvents()
    data class OnProductChanged(val product: Product) : ProductDetailsScreenUiEvents()
    object OnSaveChanges : ProductDetailsScreenUiEvents()
    object OnStartEditing : ProductDetailsScreenUiEvents()
    object OnStopEditing : ProductDetailsScreenUiEvents()
}