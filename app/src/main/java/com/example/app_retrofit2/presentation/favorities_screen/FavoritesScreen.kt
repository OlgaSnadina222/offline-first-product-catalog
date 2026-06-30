package com.example.app_retrofit2.presentation.favorities_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_retrofit2.R
import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.presentation.common.events.FavoritesUiEvent
import com.example.app_retrofit2.presentation.products_screen.ProductItem
import com.example.app_retrofit2.presentation.products_screen.backgroundForTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    viewModel: FavoritesScreenViewModel = hiltViewModel()
) {

    val favorites by viewModel.favorites.collectAsState()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val background = backgroundForTheme(preferences.theme)
        Image(
            painter = painterResource(background),
            contentDescription = "Background FavoritesScreen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
            if (favorites.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No favorite products yet",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = favorites,
                        key = { product ->
                            product.id
                        }
                    ) { product ->
                        ProductItem(
                            product = product,
                            onClickCard = {
                                onProductClick(
                                    product.id
                                )
                            },
                            onFavoriteClick = {
                                viewModel.onEvent(
                                    FavoritesUiEvent.ToggleFavorite(product.id)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { onBackClick() },
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
            containerColor = Color.White.copy(alpha = 0.9f),
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back to ProductScreen"
            )
        }
    }
}