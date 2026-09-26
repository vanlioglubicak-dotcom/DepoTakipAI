package com.example.depotakipai.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.depotakipai.data.local.DatabaseProvider
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult
import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow
import com.example.depotakipai.ui.camera.CameraScanMode
import com.example.depotakipai.ui.camera.CameraScanResult
import com.example.depotakipai.ui.camera.CameraScanScreen
import com.example.depotakipai.ui.home.HomeScreen
import com.example.depotakipai.ui.lists.ListCategory
import com.example.depotakipai.ui.lists.ListsScreen
import com.example.depotakipai.ui.products.AddProductScreen
import com.example.depotakipai.ui.products.ProductsScreen
import com.example.depotakipai.ui.returns.IncomingReturnReviewScreen
import com.example.depotakipai.ui.returns.IncomingReturnScreen
import com.example.depotakipai.ui.returns.OutgoingReturnScreen
import com.example.depotakipai.ui.returns.ReturnsScreen
import com.example.depotakipai.ui.warehouse.WarehouseScreen

private enum class AppScreen {
    HOME,
    PRODUCTS,
    ADD_PRODUCT,
    CAMERA,
    RETURNS,
    INCOMING_RETURN,
    INCOMING_RETURN_CAMERA,
    INCOMING_RETURN_REVIEW,
    OUTGOING_RETURN,
    WAREHOUSE,
    WAREHOUSE_ROW,
    WAREHOUSE_RACK,
    WAREHOUSE_LOCATION,
    LISTS,
    SETTINGS
}

@Composable
fun AppNavigation() {

    val context = LocalContext.current

    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    var cameraResult by remember {
        mutableStateOf<CameraScanResult?>(null)
    }

    var incomingReturnAnalysisResult by remember {
        mutableStateOf<IncomingReturnAnalysisResult?>(null)
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

            AppScreen.INCOMING_RETURN ->
                AppScreen.RETURNS

            AppScreen.INCOMING_RETURN_CAMERA ->
                AppScreen.INCOMING_RETURN

            AppScreen.INCOMING_RETURN_REVIEW ->
                AppScreen.INCOMING_RETURN

            AppScreen.OUTGOING_RETURN ->
                AppScreen.RETURNS

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
                },

                onIncomingReturnClick = {
                    incomingReturnAnalysisResult = null
                    currentScreen = AppScreen.INCOMING_RETURN
                },

                onOutgoingReturnClick = {
                    currentScreen = AppScreen.OUTGOING_RETURN
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
        // YENİ ÜRÜN
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
        // ÜRÜN KAMERASI
        // =========================================================

        AppScreen.CAMERA -> {

            CameraScanScreen(

                mode = CameraScanMode.PRODUCT,

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

            ReturnsScreen(

                onBack = {
                    currentScreen = AppScreen.HOME
                },

                onIncomingReturnClick = {
                    incomingReturnAnalysisResult = null
                    currentScreen = AppScreen.INCOMING_RETURN
                },

                onOutgoingReturnClick = {
                    currentScreen = AppScreen.OUTGOING_RETURN
                }
            )
        }

        // =========================================================
        // GELEN İADE
        // =========================================================

        AppScreen.INCOMING_RETURN -> {

            val database =
                DatabaseProvider.getDatabase(context)

            IncomingReturnScreen(

                database = database,

                onBack = {
                    currentScreen = AppScreen.RETURNS
                }
            )
        }

        // =========================================================
        // GELEN İADE KAMERASI
        // =========================================================

        AppScreen.INCOMING_RETURN_CAMERA -> {

            CameraScanScreen(

                mode = CameraScanMode.INCOMING_RETURN,

                onBack = {
                    currentScreen = AppScreen.INCOMING_RETURN
                },

                onScanResult = { result ->

                    incomingReturnAnalysisResult =
                        IncomingReturnAnalysisResult(
                            productCode = result.productCode,
                            systemBarcode = result.systemBarcode,
                            color = result.color,
                            size = result.size,
                            quantity = null,
                            source = null,
                            photoUri = null,
                            confidence = 0f,
                            rawText = result.rawText,
                            isValid =
                                !result.productCode.isNullOrBlank(),
                            requiresManualReview = true
                        )

                    currentScreen =
                        AppScreen.INCOMING_RETURN_REVIEW
                }
            )
        }

        // =========================================================
        // GELEN İADE KONTROL
        // =========================================================

        AppScreen.INCOMING_RETURN_REVIEW -> {

            val database =
                DatabaseProvider.getDatabase(context)

            val result =
                incomingReturnAnalysisResult

            if (result == null) {

                IncomingReturnScreen(

                    database = database,

                    onBack = {
                        currentScreen =
                            AppScreen.INCOMING_RETURN
                    }
                )

            } else {

                IncomingReturnReviewScreen(

                    database = database,

                    result = result,

                    onBack = {
                        currentScreen =
                            AppScreen.INCOMING_RETURN
                    },

                    onEdit = {
                        // Düzenleme akışı review ekranında yönetiliyor.
                    },

                    onConfirmSuccess = {

                        incomingReturnAnalysisResult = null

                        currentScreen =
                            AppScreen.INCOMING_RETURN
                    }
                )
            }
        }

        // =========================================================
        // GİDEN İADE
        // =========================================================

        AppScreen.OUTGOING_RETURN -> {

            val database =
                DatabaseProvider.getDatabase(context)

            OutgoingReturnScreen(

                database = database,

                onBack = {
                    currentScreen = AppScreen.RETURNS
                }
            )
        }

        // =========================================================
        // ANA DEPO
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
        //
        // Gerçek SettingsScreen ayrı dosyada oluşturulacak.
        // Şimdilik mevcut akış korunuyor.
        //

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
                },

                onIncomingReturnClick = {
                    incomingReturnAnalysisResult = null
                    currentScreen =
                        AppScreen.INCOMING_RETURN
                },

                onOutgoingReturnClick = {
                    currentScreen =
                        AppScreen.OUTGOING_RETURN
                }
            )
        }
    }
}