package com.example.app_retrofit2.presentation.navigation.nav_graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
    }
}