package com.example.depotakipai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.depotakipai.ui.home.HomeScreen
import com.example.depotakipai.ui.products.AddProductScreen
import com.example.depotakipai.ui.products.ProductsScreen

enum class AppScreen {
    HOME,
    PRODUCTS,
    ADD_PRODUCT,
    RETURNS,
    WAREHOUSE,
    CAMERA,
    SETTINGS
}

@Composable
fun AppNavigation() {

    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    when (currentScreen) {

        AppScreen.HOME -> {
            HomeScreen(
                onProductsClick = {
                    currentScreen = AppScreen.PRODUCTS
                }
            )
        }

        AppScreen.PRODUCTS -> {
            ProductsScreen(
                onBack = {
                    currentScreen = AppScreen.HOME
                },
                onAddProduct = {
                    currentScreen = AppScreen.ADD_PRODUCT
                }
            )
        }

        AppScreen.ADD_PRODUCT -> {
            AddProductScreen(
                onBack = {
                    currentScreen = AppScreen.PRODUCTS
                },
                onProductSaved = {
                    currentScreen = AppScreen.PRODUCTS
                }
            )
        }

        AppScreen.RETURNS -> {
            // İleride ReturnsScreen gelecek
        }

        AppScreen.WAREHOUSE -> {
            // İleride WarehouseScreen gelecek
        }

        AppScreen.CAMERA -> {
            // İleride CameraScreen gelecek
        }

        AppScreen.SETTINGS -> {
            // İleride SettingsScreen gelecek
        }
    }
}