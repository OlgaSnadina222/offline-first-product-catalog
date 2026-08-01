package com.example.app_retrofit2.presentation.common.events

sealed class FavoritesUiEvent {
    data class OnToggleFavorite(val productId: Int) : FavoritesUiEvent()
    data class OnDeleteProduct(val productId: Int) : FavoritesUiEvent()
}