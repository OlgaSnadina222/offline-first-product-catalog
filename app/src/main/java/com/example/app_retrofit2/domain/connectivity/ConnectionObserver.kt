package com.example.app_retrofit2.domain.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectionObserver {
    val isConnected: Flow<Boolean>
}