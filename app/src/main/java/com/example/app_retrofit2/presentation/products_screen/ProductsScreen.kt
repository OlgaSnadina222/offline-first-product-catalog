package com.example.app_retrofit2.presentation.products_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.app_retrofit2.R
import com.example.app_retrofit2.presentation.common.events.ProductsScreenUiEvents
import com.example.app_retrofit2.presentation.common.states.UiState
import com.example.app_retrofit2.presentation.theme.CategoryMenuColor
import com.example.app_retrofit2.presentation.theme.ProductSearchBarColor

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductsScreen(
    onProductClick: (Int) -> Unit,
    viewModel: ProductsScreenViewModel = hiltViewModel()
    ) {
    val state by viewModel.state.collectAsState()
    val categoryState by viewModel.categoriesState.collectAsState()
    val products = viewModel.products.collectAsLazyPagingItems()
    val filters by viewModel.filters.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Scaffold {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.background),
                contentDescription = "Product screen background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = CategoryMenuColor),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, Color.Transparent)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.White,
                            modifier = Modifier.size(29.dp)
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categoryState.categories.forEach { category ->
                                val isSelected = category.slug == categoryState.selectedCategorySlug
                                DropdownMenuItem(
                                    onClick = {
                                        viewModel.onEvent(
                                            ProductsScreenUiEvents.OnCategorySelected(
                                                category.slug
                                            )
                                        )
                                        expanded = false
                                    },
                                    text = {
                                        Text(
                                            text = category.name,
                                            fontWeight = if (isSelected) FontWeight.Bold
                                            else FontWeight.Normal,
                                            color = if (isSelected) Color.Black
                                            else Color.DarkGray
                                        )
                                    },
                                    modifier = Modifier.background(
                                        if (isSelected) Color.LightGray.copy(alpha = 0.3f)
                                        else Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = filters.query,
                        onValueChange = { newQuery ->
                            viewModel.onEvent(ProductsScreenUiEvents.OnQueryChange(newQuery))
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.search_products)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = ProductSearchBarColor,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = ProductSearchBarColor,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Gray
                        )
                    )
                }
                PullToRefreshBox(
                    isRefreshing =
                        products.loadState.refresh is LoadState.Loading,
                    onRefresh = {
                        products.refresh()
                    }
                ) {
                    if (products.loadState.refresh is LoadState.Loading ||
                        state is UiState.Loading && products.loadState.refresh is LoadState.Loading
                    ) {
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
                    } else if (products.loadState.refresh is LoadState.Error || state is UiState.Error) {
                        val pagingError = products.loadState.refresh as? LoadState.Error
                        val errorText = when {
                            pagingError != null -> pagingError.error.message
                            state is UiState.Error.Network -> (state as UiState.Error.Network).message
                            state is UiState.Error.Timeout -> (state as UiState.Error.Timeout).message
                            state is UiState.Error.Http -> (state as UiState.Error.Http).message
                            state is UiState.Error.Unknown -> (state as UiState.Error.Unknown).message
                            else -> {
                                UiState.Error.Unknown().message
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorText.toString(),
                                color = Color.Red
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                                .padding(horizontal = 10.dp)
                        ) {
                            items(products.itemSnapshotList) { product ->
                                ProductItem(
                                    product = product!!,
                                    onClick = { onProductClick(product.id) }
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}