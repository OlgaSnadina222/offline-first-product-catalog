package com.example.app_retrofit2.presentation.product_details_screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage
import com.example.app_retrofit2.R
import com.example.app_retrofit2.domain.model.Product
import com.example.app_retrofit2.presentation.common.events.ProductDetailsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import com.example.app_retrofit2.presentation.products_screen.backgroundForTheme
import com.example.app_retrofit2.presentation.theme.ProductCardColor
import com.example.app_retrofit2.presentation.theme.ProductTitleColor
import com.example.app_retrofit2.presentation.theme.SaveEditButton

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun ProductDetailsScreen(
    productId: Int,
    onBackClick: () -> Unit,
    viewModel: ProductDetailsScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    var editMode by rememberSaveable { mutableStateOf(false) }
    val editableProduct by viewModel.editableProduct.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val imageHeight by animateDpAsState(
        targetValue = if (editMode) 220.dp else 380.dp,
        animationSpec = tween(500),
        label = "Product image"
    )

    LaunchedEffect(Unit) {
        viewModel.onEvent(ProductDetailsScreenUiEvents.GetProductById(productId))
    }
    LaunchedEffect(Unit) {
        viewModel.saveProductSuccess.collect {
            editMode = false
            viewModel.onEvent(ProductDetailsScreenUiEvents.OnStopEditing)
        }
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
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(ProductCardColor, Color.White)
                        )
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
                        Column(modifier = Modifier.fillMaxSize())
                        {
                            Box(  
                                modifier = Modifier.fillMaxWidth().height(imageHeight)
                            ) {
                                AsyncImage(
                                    model = product.images.firstOrNull(),
                                    contentDescription = product.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                this@Column.AnimatedVisibility(
                                    visible = !editMode,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(12.dp)
                                ) {
                                    FloatingActionButton(
                                        onClick = { onBackClick() },
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
                                FloatingActionButton(
                                    onClick = {
                                        if (!editMode) editMode = true
                                        viewModel.onEvent(ProductDetailsScreenUiEvents.OnStartEditing)
                                              },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                                    containerColor = Color.White,
                                    shape = CircleShape
                                ) {
                                    AnimatedContent(
                                        targetState = editMode,
                                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                                        label = "EditIcon"
                                    ) { editMode ->
                                        Icon(
                                            imageVector = if (editMode) Icons.Default.Check else Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = if (editMode) Color.Black else Color.Gray
                                        )
                                    }
                                }
                            }
                            Box(modifier = Modifier.weight(1f).fillMaxWidth())
                            {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(scrollState)
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        AnimatedContent(
                                            targetState = editMode,
                                            transitionSpec = { fadeIn(
                                                    tween(250)) togetherWith fadeOut(
                                                    tween(250)) },
                                            label = ""
                                        ) { editMode ->
                                        if (editMode) {
                                            EditableOutlinedTextField(
                                                value = editableProduct?.title.orEmpty(),
                                                label = "",
                                                onValueChange = { title ->
                                                    editableProduct?.let { product ->
                                                        viewModel.onEvent(
                                                            ProductDetailsScreenUiEvents.OnProductChanged(
                                                                product.copy(
                                                                    title = title
                                                                )
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                        } else {
                                            Text(
                                                product.title,
                                                color = ProductTitleColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 26.sp,
                                                textAlign = TextAlign.Center)
                                        }
                                        }
                                    }
                                    Row {
                                        Text(
                                            text = stringResource(R.string.product_brand),
                                            color = Color.DarkGray,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        AnimatedContent(
                                            targetState = editMode,
                                            transitionSpec = { fadeIn(
                                                tween(250)) togetherWith fadeOut(
                                                tween(250)) },
                                            label = ""
                                        ) { editMode ->
                                        if (editMode) {
                                            EditableOutlinedTextField(
                                                value = editableProduct?.brand ?: "",
                                                label = "",
                                                onValueChange = { brand ->
                                                    editableProduct?.let { product ->
                                                        viewModel.onEvent(
                                                            ProductDetailsScreenUiEvents.OnProductChanged(
                                                            product.copy(
                                                                brand = brand
                                                            )
                                                        )
                                                        )
                                                    }
                                                }
                                            )
                                        } else {
                                            Text(
                                                product.brand,
                                                color = Color.Black,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 18.sp)
                                        }
                                        }
                                    }
                                    Row {
                                        Text(
                                            text = stringResource(R.string.product_rating),
                                            color = Color.DarkGray,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        AnimatedContent(
                                            targetState = editMode,
                                            transitionSpec = { fadeIn(
                                                tween(250)) togetherWith fadeOut(
                                                tween(250)) },
                                            label = ""
                                        ) { editMode ->
                                        if (editMode) {
                                            EditableOutlinedTextField(
                                                value = editableProduct?.rating?.toString().orEmpty(),
                                                label = "",
                                                onValueChange = { rating ->
                                                    editableProduct?.let { product ->
                                                        viewModel.onEvent(
                                                            ProductDetailsScreenUiEvents.OnProductChanged(
                                                                product.copy(
                                                                    rating = (rating.toFloatOrNull() ?: 0.0).toFloat()
                                                                )
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                        } else {
                                            Text(
                                                product.rating.toString(),
                                                color = Color.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                        }
                                    }
                                    Row {
                                        Text(
                                            text = stringResource(R.string.product_stock),
                                            color = Color.DarkGray,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        AnimatedContent(
                                            targetState = editMode,
                                            transitionSpec = { fadeIn(
                                                tween(250)) togetherWith fadeOut(
                                                tween(250)) },
                                            label = ""
                                        ) { editMode ->
                                        if (editMode) {
                                            EditableOutlinedTextField(
                                                value = editableProduct?.stock?.toString().orEmpty(),
                                                label = "",
                                                onValueChange = { stock ->
                                                    editableProduct?.let { product ->
                                                        viewModel.onEvent(
                                                            ProductDetailsScreenUiEvents.OnProductChanged(
                                                                product.copy(
                                                                    stock = stock.toIntOrNull() ?: 0
                                                                )
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                        } else {
                                            Text(
                                                product.stock.toString(),
                                                color = Color.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                        }
                                    }
                                    Row {
                                        Text(
                                            text = stringResource(R.string.product_price),
                                            color = Color.DarkGray,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        AnimatedContent(
                                            targetState = editMode,
                                            transitionSpec = { fadeIn(
                                                tween(250)) togetherWith fadeOut(
                                                tween(250)) },
                                            label = ""
                                        ) { editMode ->
                                        if (editMode) {
                                            EditableOutlinedTextField(
                                                value = editableProduct?.price?.toString().orEmpty(),
                                                label = "",
                                                onValueChange = { price ->
                                                    editableProduct?.let { product ->
                                                        viewModel.onEvent(
                                                            ProductDetailsScreenUiEvents.OnProductChanged(
                                                                product.copy(
                                                                    price = (price.toDoubleOrNull() ?: 0.00).toFloat()
                                                                )
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                        } else {
                                            Text(
                                                "$${product.price}",
                                                color = Color.Red,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp
                                            )
                                        }
                                        }
                                    }
                                    Row {
                                        AnimatedContent(
                                            targetState = editMode,
                                            transitionSpec = { fadeIn(
                                                tween(250)) togetherWith fadeOut(
                                                tween(250)) },
                                            label = ""
                                        ) { editMode ->
                                        if (editMode) {
                                            EditableOutlinedTextField(
                                                value = editableProduct?.description ?: "",
                                                label = "",
                                                onValueChange = { description ->
                                                    editableProduct?.let { product ->
                                                        viewModel.onEvent(
                                                            ProductDetailsScreenUiEvents.OnProductChanged(
                                                                product.copy(
                                                                    description = description
                                                                )
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                        } else {
                                            Text(
                                                text = product.description,
                                                fontWeight = FontWeight.Light,
                                                color = Color.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                                this@Column.AnimatedVisibility(
                                    visible = editMode,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    enter = slideInVertically { it } + fadeIn(),
                                    exit = slideOutVertically { it } + fadeOut()
                                ) {
                                    Button(
                                        onClick = { viewModel.onEvent(
                                            ProductDetailsScreenUiEvents.OnSaveChanges
                                        ) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SaveEditButton,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 8.dp
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(R.string.save_changes),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.SemiBold
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
}

