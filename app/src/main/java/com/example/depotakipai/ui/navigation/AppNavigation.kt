package com.example.depotakipai.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.depotakipai.ui.camera.CameraScanResult
import com.example.depotakipai.ui.camera.CameraScanScreen
import com.example.depotakipai.ui.home.HomeScreen
import com.example.depotakipai.ui.lists.ListCategory
import com.example.depotakipai.ui.lists.ListsScreen
import com.example.depotakipai.ui.products.AddProductScreen
import com.example.depotakipai.ui.products.ProductsScreen
import com.example.depotakipai.ui.warehouse.WarehouseScreen

private enum class AppScreen {
    HOME,
    PRODUCTS,
    ADD_PRODUCT,
    CAMERA,
    RETURNS,
    WAREHOUSE,
    LISTS,
    SETTINGS
}

@Composable
fun AppNavigation() {

    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    var cameraResult by remember {
        mutableStateOf<CameraScanResult?>(null)
    }

    BackHandler(
        enabled = currentScreen != AppScreen.HOME
    ) {
        currentScreen = when (currentScreen) {

            AppScreen.PRODUCTS ->
                AppScreen.HOME

            AppScreen.ADD_PRODUCT ->
                AppScreen.PRODUCTS

            AppScreen.CAMERA ->
                AppScreen.ADD_PRODUCT

            AppScreen.RETURNS ->
                AppScreen.HOME

            AppScreen.WAREHOUSE ->
                AppScreen.HOME

            AppScreen.LISTS ->
                AppScreen.WAREHOUSE

            AppScreen.SETTINGS ->
                AppScreen.HOME

            AppScreen.HOME ->
                AppScreen.HOME
        }
    }

    when (currentScreen) {

        // =========================================================
        // ANA SAYFA
        // =========================================================

        AppScreen.HOME -> {

            HomeScreen(
                onProductsClick = {
                    currentScreen = AppScreen.PRODUCTS
                },

                onAddProductClick = {
                    cameraResult = null
                    currentScreen = AppScreen.ADD_PRODUCT
                },

                onReturnsClick = {
                    currentScreen = AppScreen.RETURNS
                },

                onSettingsClick = {
                    currentScreen = AppScreen.SETTINGS
                }
            )
        }

        // =========================================================
        // ÜRÜNLER
        // =========================================================

        AppScreen.PRODUCTS -> {

            ProductsScreen(
                onBack = {
                    currentScreen = AppScreen.HOME
                },

                onAddProduct = {
                    cameraResult = null
                    currentScreen = AppScreen.ADD_PRODUCT
                },

                onReturnsClick = {
                    currentScreen = AppScreen.RETURNS
                },

                onSettingsClick = {
                    currentScreen = AppScreen.SETTINGS
                }
            )
        }

        // =========================================================
        // ÜRÜN EKLE
        // =========================================================

        AppScreen.ADD_PRODUCT -> {

            AddProductScreen(
                onProductSaved = {
                    cameraResult = null
                    currentScreen = AppScreen.PRODUCTS
                },

                onBack = {
                    cameraResult = null
                    currentScreen = AppScreen.PRODUCTS
                },

                onCameraClick = {
                    cameraResult = null
                    currentScreen = AppScreen.CAMERA
                },

                cameraResult = cameraResult
            )
        }

        // =========================================================
        // KAMERA
        // =========================================================

        AppScreen.CAMERA -> {

            CameraScanScreen(
                onBack = {
                    currentScreen = AppScreen.ADD_PRODUCT
                },

                onScanResult = { result ->
                    cameraResult = result
                    currentScreen = AppScreen.ADD_PRODUCT
                }
            )
        }

        // =========================================================
        // İADELER
        // =========================================================

        AppScreen.RETURNS -> {

            HomeScreen(
                onProductsClick = {
                    currentScreen = AppScreen.PRODUCTS
                },

                onAddProductClick = {
                    cameraResult = null
                    currentScreen = AppScreen.ADD_PRODUCT
                },

                onReturnsClick = {
                    currentScreen = AppScreen.RETURNS
                },

                onSettingsClick = {
                    currentScreen = AppScreen.SETTINGS
                }
            )
        }

        // =========================================================
        // DEPO
        // =========================================================

        AppScreen.WAREHOUSE -> {

            WarehouseScreen(

                onBack = {
                    currentScreen = AppScreen.HOME
                },

                onListsClick = {
                    currentScreen = AppScreen.LISTS
                },

                onProductsClick = {
                    currentScreen = AppScreen.PRODUCTS
                },

                onLocationClick = {
                    // Raf / konum sistemi sonraki aşamada
                },

                onStockClick = {
                    // Stok ekranı sonraki aşamada
                }
            )
        }

        // =========================================================
        // LİSTELER
        // =========================================================

        AppScreen.LISTS -> {

            ListsScreen(

                onBack = {
                    currentScreen = AppScreen.WAREHOUSE
                },

                onCategoryClick = { category ->

                    when (category) {

                        ListCategory.ANA_LISTE -> {
                            // Sonraki aşamada
                        }

                        ListCategory.YENI_GELEN -> {
                            // Sonraki aşamada
                        }

                        ListCategory.GIDEN -> {
                            // Sonraki aşamada
                        }

                        ListCategory.GIDEN_IADE -> {
                            // Sonraki aşamada
                        }

                        ListCategory.GELEN_IADE -> {
                            // Sonraki aşamada
                        }
                    }
                }
            )
        }

        // =========================================================
        // AYARLAR
        // =========================================================

        AppScreen.SETTINGS -> {

            HomeScreen(
                onProductsClick = {
                    currentScreen = AppScreen.PRODUCTS
                },

                onAddProductClick = {
                    cameraResult = null
                    currentScreen = AppScreen.ADD_PRODUCT
                },

                onReturnsClick = {
                    currentScreen = AppScreen.RETURNS
                },

                onSettingsClick = {
                    currentScreen = AppScreen.SETTINGS
                }
            )
        }
    }
}