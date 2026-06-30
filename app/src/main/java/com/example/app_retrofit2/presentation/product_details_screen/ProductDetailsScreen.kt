package com.example.app_retrofit2.presentation.product_details_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.app_retrofit2.R
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.domain.model.ThemeMode
import com.example.app_retrofit2.domain.model.UserPreferences
import com.example.app_retrofit2.presentation.common.events.ProductDetailsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import com.example.app_retrofit2.presentation.products_screen.backgroundForTheme
import com.example.app_retrofit2.presentation.theme.ProductCardColor
import com.example.app_retrofit2.presentation.theme.ProductTitleColor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Composable
fun ProductDetailsScreen(
    productId: Int,
    onBackClick: () -> Unit,
    viewModel: ProductDetailsScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(ProductDetailsScreenUiEvents.GetProductById(productId))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val background = backgroundForTheme(preferences.theme)
        Image(
            painter = painterResource(background),
            contentDescription = "ProductDetailsScreen background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Card(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(brush = Brush.horizontalGradient(
                        colors = listOf(ProductCardColor, Color.White))
                    )
            ) {
                when (state) {
                    UiState.Loading -> {
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
                    is UiState.Error -> {
                        val message = when (state) {
                            is UiState.Error.Network -> "No internet connection. Check your Wi-Fi."
                            is UiState.Error.Timeout -> "Request timed out. Try again."
                            is UiState.Error.Http -> (state as UiState.Error.Http).message
                            is UiState.Error.Unknown -> "Something went wrong."
                            else -> {}
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = message.toString(),
                                color = Color.Red
                            )
                        }
                    }
                    is UiState.Success -> {
                        val product = (state as UiState.Success<Product>).data
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxHeight(0.5f)
                            ) {
                                AsyncImage(
                                    model = product.images.firstOrNull(),
                                    contentDescription = product.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
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
                        Column(
                            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    product.title,
                                    color = ProductTitleColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Row {
                                Text(
                                    text = stringResource(R.string.product_brand),
                                    color = Color.DarkGray,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    product.brand,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp
                                )
                            }
                            Row {
                                Text(
                                    text = stringResource(R.string.product_rating),
                                    color = Color.DarkGray,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    product.rating.toString(),
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                            Row {
                                Text(
                                    text = stringResource(R.string.product_stock),
                                    color = Color.DarkGray,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    product.stock.toString(),
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                            Row {
                                Text(
                                    text = stringResource(R.string.product_price),
                                    color = Color.DarkGray,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "$${product.price}",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                            Row {
                                Text(
                                    text = product.description,
                                    fontWeight = FontWeight.Light,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        }
                    }
                }
            }

        }
    }
}
