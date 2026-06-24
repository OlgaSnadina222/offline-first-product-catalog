package com.example.app_retrofit2.presentation.products_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.example.app_retrofit2.presentation.common.states.UiState

@Composable
fun PullToRefreshCustomIndicator(
    pullState: PullToRefreshState,
    isRefreshing: Boolean
) {
    val fraction = pullState.distanceFraction
    val offset = (fraction * 50).dp
    val rotation = fraction * 300

    if (fraction > 0f && !isRefreshing) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Icon(
                modifier = Modifier
                    .padding(top = offset)
                    .alpha(fraction)
                    .rotate(rotation)
                    .background(color = Color.White, shape = CircleShape),
                imageVector = Icons.Default.Refresh,
                tint = Color.Red,
                contentDescription = null
            )
        }
    }
    if (isRefreshing) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                strokeWidth = 7.dp,
                color = Color.Red
            )
        }
    }
}