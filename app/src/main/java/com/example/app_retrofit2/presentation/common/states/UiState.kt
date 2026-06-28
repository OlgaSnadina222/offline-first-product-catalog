package com.example.app_retrofit2.presentation.common.states

import com.example.app_retrofit2.R

sealed class UiState<out T> {
    object Loading: UiState<Nothing>()
    data class Success<T>(val data: T): UiState<T>()
    sealed class Error: UiState<Nothing>() {
        data class Network(val message: String = "No internet connection") : Error()
        data class Timeout(val message: String = "Request timed out - try again") : Error()
        data class Http(val code: Int, val message: String = "Server error") : Error()
        data class Unknown(val message: String = "Something went wrong") : Error()
    }
}