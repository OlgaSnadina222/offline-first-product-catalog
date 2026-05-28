package com.example.app_retrofit2.presentation.common.states

import com.example.app_retrofit2.R

sealed class UiState<out T> {
    object Loading: UiState<Nothing>()
    data class Success<T>(val data: T): UiState<T>()
    sealed class Error: UiState<Nothing>() {
        data class Network(val message: Int = R.string.no_internet_connection) : Error()
        data class Timeout(val message: Int = R.string.request_timeout) : Error()
        data class Http(val code: Int, val message: String = R.string.server_error.toString()) : Error()
        data class Unknown(val message: String = R.string.smth_went_wrong.toString()) : Error()
    }
}