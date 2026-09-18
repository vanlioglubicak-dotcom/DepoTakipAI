package com.example.depotakipai.ui.navigation

import androidx.compose.runtime.Composable
import com.example.depotakipai.ui.home.HomeScreen

enum class AppScreen {
    HOME,
    PRODUCTS,
    RETURNS,
    WAREHOUSE,
    CAMERA,
    SETTINGS
}

@Composable
fun AppNavigation() {
    HomeScreen()
}