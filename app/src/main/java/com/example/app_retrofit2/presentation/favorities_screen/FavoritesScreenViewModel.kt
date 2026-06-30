package com.example.app_retrofit2.presentation.favorities_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_retrofit2.domain.model.UserPreferences
import com.example.app_retrofit2.domain.usecase.GetFavoriteProductsUseCase
import com.example.app_retrofit2.domain.usecase.ToggleFavoriteUseCase
import com.example.app_retrofit2.domain.usecase.preferences.GetUserPreferencesUseCase
import com.example.app_retrofit2.presentation.common.events.FavoritesUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesScreenViewModel @Inject constructor(
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {

    val favorites = getFavoriteProductsUseCase().stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val preferences = getUserPreferencesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences()
    )

    fun onEvent(event: FavoritesUiEvent) {
        when (event) {
            is FavoritesUiEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(event.productId)
                }
            }
        }
    }
}