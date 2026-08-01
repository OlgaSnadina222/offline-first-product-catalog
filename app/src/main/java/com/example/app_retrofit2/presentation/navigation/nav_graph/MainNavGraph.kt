package com.example.app_retrofit2.presentation.navigation.nav_graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.app_retrofit2.presentation.favorites_screen.FavoritesScreen
import com.example.app_retrofit2.presentation.navigation.favorites_screen.FavoritesScreenNavData
import com.example.app_retrofit2.presentation.navigation.product_details_screen.ProductDetailsScreenNavData
import com.example.app_retrofit2.presentation.navigation.products_screen.ProductsScreenNavData
import com.example.app_retrofit2.presentation.product_details_screen.ProductDetailsScreen
import com.example.app_retrofit2.presentation.products_screen.ProductsScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ProductsScreenNavData,
        modifier = modifier
    ) {
        composable<ProductsScreenNavData> {
            ProductsScreen(
                onProductClick = { productId ->
                    navController.navigate(
                        ProductDetailsScreenNavData(productId)
                    )
                },
                onFavoritesClick = {
                    navController.navigate(FavoritesScreenNavData)
                }
            )
        }
        composable<ProductDetailsScreenNavData> { backStackEntry ->
            val productId = backStackEntry.toRoute<ProductDetailsScreenNavData>().productId
            ProductDetailsScreen(
                productId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<FavoritesScreenNavData> {
            FavoritesScreen(
                onProductClick = { productId ->
                    navController.navigate(
                        ProductDetailsScreenNavData(productId)
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}