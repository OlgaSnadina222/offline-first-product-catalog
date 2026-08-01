package com.example.app_retrofit2.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.app_retrofit2.data.sync.SyncScheduler
import com.example.app_retrofit2.presentation.navigation.nav_graph.MainNavGraph
import com.example.app_retrofit2.presentation.products_screen.ProductsScreenViewModel
import com.example.app_retrofit2.presentation.theme.Lesson_Retrofit2Theme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ProductsScreenViewModel by viewModels()
    @Inject
    lateinit var syncScheduler: SyncScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        syncScheduler.schedulePeriodicSync()
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Lesson_Retrofit2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavGraph(navController,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }

    }
}
