package com.example.depotakipai.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow
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
    WAREHOUSE_ROW,
    WAREHOUSE_RACK,
    WAREHOUSE_LOCATION,
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

    var selectedRow by remember {
        mutableStateOf<WarehouseRow?>(null)
    }

    var selectedRack by remember {
        mutableStateOf<WarehouseRack?>(null)
    }

    var selectedLocation by remember {
        mutableStateOf<WarehouseLocation?>(null)
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

            AppScreen.WAREHOUSE_ROW ->
                AppScreen.WAREHOUSE

            AppScreen.WAREHOUSE_RACK ->
                AppScreen.WAREHOUSE_ROW

            AppScreen.WAREHOUSE_LOCATION ->
                AppScreen.WAREHOUSE_RACK

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
                },

                onWarehouseClick = {
                    currentScreen = AppScreen.WAREHOUSE
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
                },

                onWarehouseClick = {
                    currentScreen = AppScreen.WAREHOUSE
                }
            )
        }

        // =========================================================
        // DEPO ANA EKRANI
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
                },

                onStockClick = {
                },

                onRowClick = { row ->

                    selectedRow = row
                    selectedRack = null
                    selectedLocation = null

                    currentScreen =
                        AppScreen.WAREHOUSE_ROW
                },

                onAddRowClick = {
                },

                onSearchClick = {
                }
            )
        }

        // =========================================================
        // DEPO SIRA
        // =========================================================

        AppScreen.WAREHOUSE_ROW -> {

            WarehouseRowNavigation(

                row = selectedRow
                    ?: WarehouseRow(
                        rowNumber = 1
                    ),

                onBack = {
                    currentScreen =
                        AppScreen.WAREHOUSE
                },

                onRackClick = { rack ->

                    selectedRack = rack
                    selectedLocation = null

                    currentScreen =
                        AppScreen.WAREHOUSE_RACK
                },

                onAddRackClick = {
                }
            )
        }

        // =========================================================
        // DEPO RAF
        // =========================================================

        AppScreen.WAREHOUSE_RACK -> {

            val rack = selectedRack

            if (rack == null) {

                currentScreen =
                    AppScreen.WAREHOUSE_ROW

            } else {

                WarehouseRackNavigation(

                    rack = rack,

                    onBack = {
                        currentScreen =
                            AppScreen.WAREHOUSE_ROW
                    },

                    onLocationClick = { location ->

                        selectedLocation = location

                        currentScreen =
                            AppScreen.WAREHOUSE_LOCATION
                    },

                    onAddLocationClick = {
                    }
                )
            }
        }

        // =========================================================
        // DEPO KONUM
        // =========================================================

        AppScreen.WAREHOUSE_LOCATION -> {

            val location = selectedLocation

            if (location == null) {

                currentScreen =
                    AppScreen.WAREHOUSE_RACK

            } else {

                WarehouseLocationNavigation(

                    location = location,

                    onBack = {
                        currentScreen =
                            AppScreen.WAREHOUSE_RACK
                    },

                    onProductClick = {
                    },

                    onEditClick = {
                    }
                )
            }
        }

        // =========================================================
        // LİSTELER
        // =========================================================

        AppScreen.LISTS -> {

            ListsScreen(

                onBack = {
                    currentScreen =
                        AppScreen.WAREHOUSE
                },

                onCategoryClick = { category ->

                    when (category) {

                        ListCategory.ANA_LISTE -> {
                        }

                        ListCategory.YENI_GELEN -> {
                        }

                        ListCategory.GIDEN -> {
                        }

                        ListCategory.GIDEN_IADE -> {
                        }

                        ListCategory.GELEN_IADE -> {
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
                    currentScreen =
                        AppScreen.PRODUCTS
                },

                onAddProductClick = {
                    cameraResult = null
                    currentScreen =
                        AppScreen.ADD_PRODUCT
                },

                onReturnsClick = {
                    currentScreen =
                        AppScreen.RETURNS
                },

                onSettingsClick = {
                    currentScreen =
                        AppScreen.SETTINGS
                },

                onWarehouseClick = {
                    currentScreen =
                        AppScreen.WAREHOUSE
                }
            )
        }
    }
}