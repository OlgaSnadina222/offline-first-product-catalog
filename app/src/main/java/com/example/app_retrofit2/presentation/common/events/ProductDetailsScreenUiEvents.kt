package com.example.app_retrofit2.presentation.common.events

sealed class ProductDetailsScreenUiEvents {
    data class GetProductById(val id: Int): ProductDetailsScreenUiEvents()
}