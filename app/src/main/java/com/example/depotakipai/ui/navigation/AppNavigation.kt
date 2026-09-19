package com.example.depotakipai.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depotakipai.ui.camera.CameraScanScreen
import com.example.depotakipai.ui.camera.CameraScanResult
import com.example.depotakipai.ui.home.HomeScreen
import com.example.depotakipai.ui.products.AddProductScreen
import com.example.depotakipai.ui.products.ProductsScreen

private val Background = Color(0xFFF5F5F5)
private val DarkRed = Color(0xFF8B0000)
private val Gray = Color(0xFF808080)

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

            AppScreen.RETURNS ->
                AppScreen.HOME

            AppScreen.WAREHOUSE ->
                AppScreen.HOME

            AppScreen.CAMERA ->
                AppScreen.ADD_PRODUCT

            AppScreen.SETTINGS ->
                AppScreen.HOME

            AppScreen.HOME ->
                AppScreen.HOME
        }
    }

    when (currentScreen) {

        // --------------------------------------------------
        // ANA SAYFA
        // --------------------------------------------------

        AppScreen.HOME -> {

            HomeScreen(
                onProductsClick = {
                    currentScreen = AppScreen.PRODUCTS
                },
                onAddProductClick = {
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

        // --------------------------------------------------
        // ÜRÜNLER
        // --------------------------------------------------

        AppScreen.PRODUCTS -> {

            ProductsScreen(
                onBack = {
                    currentScreen = AppScreen.HOME
                },
                onAddProduct = {
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

        // --------------------------------------------------
        // ÜRÜN EKLE
        // --------------------------------------------------

        AppScreen.ADD_PRODUCT -> {

            AddProductScreen(
                onBack = {
                    currentScreen = AppScreen.PRODUCTS
                },
                onProductSaved = {
                    currentScreen = AppScreen.PRODUCTS
                },
                onCameraClick = {

                    cameraResult = null

                    currentScreen =
                        AppScreen.CAMERA
                }
            )
        }

        // --------------------------------------------------
        // GERÇEK KAMERA
        // --------------------------------------------------

        AppScreen.CAMERA -> {

            CameraScanScreen(
                onBack = {

                    currentScreen =
                        AppScreen.ADD_PRODUCT
                },
                onScanResult = { result ->

                    cameraResult = result
                }
            )
        }

        // --------------------------------------------------
        // İADELER
        // --------------------------------------------------

        AppScreen.RETURNS -> {

            PlaceholderScreen(
                title = "İADELER",
                description =
                    "İade sistemi bu bölümde oluşturulacak.",
                onBack = {
                    currentScreen = AppScreen.HOME
                }
            )
        }

        // --------------------------------------------------
        // DEPO
        // --------------------------------------------------

        AppScreen.WAREHOUSE -> {

            PlaceholderScreen(
                title = "DEPO",
                description =
                    "Depo ve raf sistemi bu bölümde oluşturulacak.",
                onBack = {
                    currentScreen = AppScreen.HOME
                }
            )
        }

        // --------------------------------------------------
        // AYARLAR
        // --------------------------------------------------

        AppScreen.SETTINGS -> {

            PlaceholderScreen(
                title = "AYARLAR",
                description =
                    "Uygulama ayarları bu bölümde oluşturulacak.",
                onBack = {
                    currentScreen = AppScreen.HOME
                }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    description: String,
    onBack: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DarkRed
            )

            Text(
                text = description,
                modifier = Modifier.padding(
                    top = 12.dp
                ),
                fontSize = 14.sp,
                color = Gray
            )

            Button(
                onClick = onBack,
                modifier = Modifier.padding(
                    top = 24.dp
                )
            ) {

                Text(
                    text = "Geri"
                )
            }
        }
    }
}