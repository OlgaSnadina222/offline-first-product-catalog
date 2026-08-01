package com.example.app_retrofit2.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.app_retrofit2.domain.connectivity.ConnectionObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityObserver @Inject constructor(
    context: Context
): ConnectionObserver {
    val connectionManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    override val isConnected: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback(){

            override fun onAvailable(network: Network) {
                trySend(true)
                super.onAvailable(network)
            }

            override fun onLost(network: Network) {
                trySend(false)
                super.onLost(network)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectionManager.registerNetworkCallback(request, callback)

        val isConnected = connectionManager.activeNetwork?.let { network ->
            connectionManager.getNetworkCapabilities(network)?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false

        trySend(isConnected)

        awaitClose {
            connectionManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}