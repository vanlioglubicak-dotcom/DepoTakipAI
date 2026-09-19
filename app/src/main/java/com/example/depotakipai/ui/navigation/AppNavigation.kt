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
import com.example.depotakipai.ui.products.AddProductScreen
import com.example.depotakipai.ui.products.ProductsScreen

private enum class AppScreen {
    HOME,
    PRODUCTS,
    ADD_PRODUCT,
    CAMERA,
    RETURNS,
    WAREHOUSE,
    SETTINGS
}

@Composable
fun AppNavigation() {

    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    /*
     * Kameradan gelen son okuma sonucu.
     *
     * Kamera ekranında EKLE'ye basıldığında buraya gelir.
     * Daha sonra Ürün Ekle ekranına aktarılır.
     */
    var cameraResult by remember {
        mutableStateOf<CameraScanResult?>(null)
    }

    /*
     * Android geri tuşu yönetimi
     */
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

            AppScreen.SETTINGS ->
                AppScreen.HOME

            AppScreen.HOME ->
                AppScreen.HOME
        }
    }

    when (currentScreen) {

        // ---------------------------------------------------------
        // ANA SAYFA
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // ÜRÜNLER
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // ÜRÜN EKLE
        // ---------------------------------------------------------

        AppScreen.ADD_PRODUCT -> {

            AddProductScreen(

                /*
                 * Ürün kaydedildiğinde ürün listesine dön
                 */
                onProductSaved = {
                    cameraResult = null
                    currentScreen = AppScreen.PRODUCTS
                },

                /*
                 * Geri
                 */
                onBack = {
                    cameraResult = null
                    currentScreen = AppScreen.PRODUCTS
                },

                /*
                 * Kamera butonu
                 */
                onCameraClick = {

                    /*
                     * Önceki kamera sonucunu temizliyoruz.
                     *
                     * Böylece yeni kamera okuması eski bilgileri
                     * ürün formuna taşımaz.
                     */
                    cameraResult = null

                    currentScreen = AppScreen.CAMERA
                },

                /*
                 * Kameradan gelen sonuç.
                 *
                 * AddProductScreen bu sonucu alıp:
                 *
                 * Trendyol Ürün Kodu
                 * Renk
                 * Beden
                 * Sistem Barkodu
                 *
                 * alanlarına aktaracak.
                 */
                cameraResult = cameraResult
            )
        }

        // ---------------------------------------------------------
        // KAMERA
        // ---------------------------------------------------------

        AppScreen.CAMERA -> {

            CameraScanScreen(

                /*
                 * Kamera ekranında geri
                 */
                onBack = {
                    currentScreen = AppScreen.ADD_PRODUCT
                },

                /*
                 * Kamera 5 saniyelik okuma sonucunu
                 * EKLE butonuyla buraya gönderir.
                 */
                onScanResult = { result ->

                    /*
                     * Sonucu sakla
                     */
                    cameraResult = result

                    /*
                     * Ürün formuna geri dön
                     */
                    currentScreen = AppScreen.ADD_PRODUCT
                }
            )
        }

        // ---------------------------------------------------------
        // İADELER
        // ---------------------------------------------------------

        AppScreen.RETURNS -> {

            /*
             * İade ekranını sonraki adımda oluşturacağız.
             */
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

        // ---------------------------------------------------------
        // DEPO
        // ---------------------------------------------------------

        AppScreen.WAREHOUSE -> {

            /*
             * Depo ekranını sonraki aşamada oluşturacağız.
             */
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

        // ---------------------------------------------------------
        // AYARLAR
        // ---------------------------------------------------------

        AppScreen.SETTINGS -> {

            /*
             * Ayarlar ekranını sonraki aşamada oluşturacağız.
             */
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