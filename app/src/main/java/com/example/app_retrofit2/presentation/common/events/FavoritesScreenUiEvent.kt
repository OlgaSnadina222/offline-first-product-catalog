package com.example.app_retrofit2.presentation.common.events

sealed interface FavoritesUiEvent {

    data class ToggleFavorite(val productId: Int) : FavoritesUiEvent
}