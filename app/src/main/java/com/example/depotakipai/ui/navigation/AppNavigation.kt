package com.example.depotakipai.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.depotakipai.data.local.DatabaseProvider
import com.example.depotakipai.domain.model.CameraScanBatch
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult
import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow
import com.example.depotakipai.ui.camera.ContinuousCameraScanScreen
import com.example.depotakipai.ui.components.BottomNavigationBar
import com.example.depotakipai.ui.components.BottomNavigationItemType
import com.example.depotakipai.ui.components.QuickActionMenu
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
    OUTGOING_RETURN,

    INCOMING_RETURN_REVIEW,

    WAREHOUSE,
    WAREHOUSE_ROW,
    WAREHOUSE_RACK,
    WAREHOUSE_LOCATION,

    LISTS,
    SETTINGS
}

private enum class CameraFlow {
    NONE,
    NEW_PRODUCT,
    INCOMING_RETURN,
    OUTGOING_RETURN
}

@Composable
fun AppNavigation() {

    val context = LocalContext.current

    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    var quickActionVisible by remember {
        mutableStateOf(false)
    }

    /*
     * =========================================================
     * ORTAK KAMERA AKIŞI
     * =========================================================
     */

    var cameraFlow by remember {
        mutableStateOf(CameraFlow.NONE)
    }

    var cameraBatch by remember {
        mutableStateOf(CameraScanBatch())
    }

    /*
     * Eski ürün kamera sonucu artık kullanılmıyor.
     * Yeni sistem CameraScanBatch kullanıyor.
     */

    var incomingReturnAnalysisResult by remember {
        mutableStateOf<IncomingReturnAnalysisResult?>(null)
    }

    /*
     * =========================================================
     * DEPO SEÇİMLERİ
     * =========================================================
     */

    var selectedRow by remember {
        mutableStateOf<WarehouseRow?>(null)
    }

    var selectedRack by remember {
        mutableStateOf<WarehouseRack?>(null)
    }

    var selectedLocation by remember {
        mutableStateOf<WarehouseLocation?>(null)
    }

    /*
     * =========================================================
     * GERİ TUŞU
     * =========================================================
     */

    BackHandler(
        enabled =
            currentScreen != AppScreen.HOME ||
                    quickActionVisible
    ) {

        if (quickActionVisible) {

            quickActionVisible = false

        } else {

            currentScreen = when (currentScreen) {

                AppScreen.PRODUCTS ->
                    AppScreen.HOME

                AppScreen.ADD_PRODUCT ->
                    AppScreen.PRODUCTS

                AppScreen.CAMERA -> {

                    when (cameraFlow) {

                        CameraFlow.NEW_PRODUCT ->
                            AppScreen.ADD_PRODUCT

                        CameraFlow.INCOMING_RETURN ->
                            AppScreen.RETURNS

                        CameraFlow.OUTGOING_RETURN ->
                            AppScreen.RETURNS

                        CameraFlow.NONE ->
                            AppScreen.HOME
                    }
                }

                AppScreen.RETURNS ->
                    AppScreen.HOME

                AppScreen.INCOMING_RETURN ->
                    AppScreen.RETURNS

                AppScreen.OUTGOING_RETURN ->
                    AppScreen.RETURNS

                AppScreen.INCOMING_RETURN_REVIEW ->
                    AppScreen.INCOMING_RETURN

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
    }

    /*
     * =========================================================
     * ANA CONTAINER
     * =========================================================
     */

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        /*
         * =====================================================
         * SAYFA İÇERİĞİ
         * =====================================================
         */

        when (currentScreen) {

            /*
             * =================================================
             * ANA SAYFA
             * =================================================
             */

            AppScreen.HOME -> {

                HomeScreen(

                    onProductsClick = {

                        quickActionVisible = false

                        currentScreen =
                            AppScreen.PRODUCTS
                    },

                    onAddProductClick = {

                        quickActionVisible = false

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.NEW_PRODUCT

                        currentScreen =
                            AppScreen.CAMERA
                    },

                    onReturnsClick = {

                        quickActionVisible = false

                        currentScreen =
                            AppScreen.RETURNS
                    },

                    onSettingsClick = {

                        quickActionVisible = false

                        currentScreen =
                            AppScreen.SETTINGS
                    },

                    onWarehouseClick = {

                        quickActionVisible = false

                        currentScreen =
                            AppScreen.WAREHOUSE
                    },

                    onIncomingReturnClick = {

                        quickActionVisible = false

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.INCOMING_RETURN

                        currentScreen =
                            AppScreen.CAMERA
                    },

                    onOutgoingReturnClick = {

                        quickActionVisible = false

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.OUTGOING_RETURN

                        currentScreen =
                            AppScreen.CAMERA
                    }
                )
            }

            /*
             * =================================================
             * ÜRÜNLER
             * =================================================
             */

            AppScreen.PRODUCTS -> {

                ProductsScreen(

                    onBack = {

                        currentScreen =
                            AppScreen.HOME
                    },

                    onAddProduct = {

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.NEW_PRODUCT

                        currentScreen =
                            AppScreen.CAMERA
                    },

                    onReturnsClick = {

                        currentScreen =
                            AppScreen.RETURNS
                    },

                    onSettingsClick = {

                        currentScreen =
                            AppScreen.SETTINGS
                    }
                )
            }

            /*
             * =================================================
             * YENİ ÜRÜN FORMU
             * =================================================
             */

            AppScreen.ADD_PRODUCT -> {

                AddProductScreen(

                    onProductSaved = {

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.NONE

                        currentScreen =
                            AppScreen.PRODUCTS
                    },

                    onBack = {

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.NONE

                        currentScreen =
                            AppScreen.PRODUCTS
                    },

                    onCameraClick = {

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.NEW_PRODUCT

                        currentScreen =
                            AppScreen.CAMERA
                    },

                    cameraResult = null
                )
            }

            /*
             * =================================================
             * ORTAK SÜREKLİ KAMERA
             *
             * YENİ ÜRÜN
             * GELEN İADE
             * GİDEN İADE
             *
             * ÜÇÜ DE AYNI KAMERA
             * =================================================
             */

            AppScreen.CAMERA -> {

                ContinuousCameraScanScreen(

                    onBack = {

                        currentScreen = when (cameraFlow) {

                            CameraFlow.NEW_PRODUCT ->
                                AppScreen.ADD_PRODUCT

                            CameraFlow.INCOMING_RETURN ->
                                AppScreen.RETURNS

                            CameraFlow.OUTGOING_RETURN ->
                                AppScreen.RETURNS

                            CameraFlow.NONE ->
                                AppScreen.HOME
                        }
                    },

                    onFinished = { batch ->

                        cameraBatch =
                            batch

                        currentScreen = when (cameraFlow) {

                            CameraFlow.NEW_PRODUCT ->
                                AppScreen.ADD_PRODUCT

                            CameraFlow.INCOMING_RETURN ->
                                AppScreen.INCOMING_RETURN

                            CameraFlow.OUTGOING_RETURN ->
                                AppScreen.OUTGOING_RETURN

                            CameraFlow.NONE ->
                                AppScreen.HOME
                        }
                    }
                )
            }

            /*
             * =================================================
             * İADELER ANA SAYFA
             * =================================================
             */

            AppScreen.RETURNS -> {

                ReturnsScreen(

                    onBack = {

                        currentScreen =
                            AppScreen.HOME
                    },

                    onIncomingReturnClick = {

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.INCOMING_RETURN

                        currentScreen =
                            AppScreen.CAMERA
                    },

                    onOutgoingReturnClick = {

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.OUTGOING_RETURN

                        currentScreen =
                            AppScreen.CAMERA
                    }
                )
            }

            /*
             * =================================================
             * GELEN İADE LİSTESİ
             * =================================================
             */

            AppScreen.INCOMING_RETURN -> {

                val database =
                    DatabaseProvider.getDatabase(context)

                IncomingReturnScreen(

                    database = database,

                    onBack = {

                        currentScreen =
                            AppScreen.RETURNS
                    }
                )
            }

            /*
             * =================================================
             * GELEN İADE ONAY
             * =================================================
             */

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
                            // Düzenleme akışı korunuyor.
                        },

                        onConfirmSuccess = {

                            incomingReturnAnalysisResult =
                                null

                            currentScreen =
                                AppScreen.INCOMING_RETURN
                        }
                    )
                }
            }

            /*
             * =================================================
             * GİDEN İADE
             * =================================================
             */

            AppScreen.OUTGOING_RETURN -> {

                val database =
                    DatabaseProvider.getDatabase(context)

                OutgoingReturnScreen(

                    database = database,

                    onBack = {

                        currentScreen =
                            AppScreen.RETURNS
                    }
                )
            }

            /*
             * =================================================
             * DEPO
             * =================================================
             */

            AppScreen.WAREHOUSE -> {

                WarehouseScreen(

                    onBack = {

                        currentScreen =
                            AppScreen.HOME
                    },

                    onListsClick = {

                        currentScreen =
                            AppScreen.LISTS
                    },

                    onProductsClick = {

                        currentScreen =
                            AppScreen.PRODUCTS
                    },

                    onLocationClick = {
                    },

                    onStockClick = {
                    },

                    onRowClick = { row ->

                        selectedRow =
                            row

                        selectedRack =
                            null

                        selectedLocation =
                            null

                        currentScreen =
                            AppScreen.WAREHOUSE_ROW
                    },

                    onAddRowClick = {
                    },

                    onSearchClick = {
                    }
                )
            }

            /*
             * =================================================
             * DEPO SIRA
             * =================================================
             */

            AppScreen.WAREHOUSE_ROW -> {

                WarehouseRowNavigation(

                    row =
                        selectedRow
                            ?: WarehouseRow(
                                rowNumber = 1
                            ),

                    onBack = {

                        currentScreen =
                            AppScreen.WAREHOUSE
                    },

                    onRackClick = { rack ->

                        selectedRack =
                            rack

                        selectedLocation =
                            null

                        currentScreen =
                            AppScreen.WAREHOUSE_RACK
                    },

                    onAddRackClick = {
                    }
                )
            }

            /*
             * =================================================
             * DEPO RAF
             * =================================================
             */

            AppScreen.WAREHOUSE_RACK -> {

                val rack =
                    selectedRack

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

                            selectedLocation =
                                location

                            currentScreen =
                                AppScreen.WAREHOUSE_LOCATION
                        },

                        onAddLocationClick = {
                        }
                    )
                }
            }

            /*
             * =================================================
             * DEPO KONUM
             * =================================================
             */

            AppScreen.WAREHOUSE_LOCATION -> {

                val location =
                    selectedLocation

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

            /*
             * =================================================
             * LİSTELER
             * =================================================
             */

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

            /*
             * =================================================
             * AYARLAR
             * =================================================
             */

            AppScreen.SETTINGS -> {

                HomeScreen(

                    onProductsClick = {

                        currentScreen =
                            AppScreen.PRODUCTS
                    },

                    onAddProductClick = {

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.NEW_PRODUCT

                        currentScreen =
                            AppScreen.CAMERA
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

        /*
         * =========================================================
         * HIZLI İŞLEMLER
         * =========================================================
         */

        QuickActionMenu(

            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                ),

            visible =
                quickActionVisible,

            onNewProductClick = {

                quickActionVisible =
                    false

                cameraBatch =
                    CameraScanBatch()

                cameraFlow =
                    CameraFlow.NEW_PRODUCT

                currentScreen =
                    AppScreen.CAMERA
            },

            onIncomingReturnClick = {

                quickActionVisible =
                    false

                cameraBatch =
                    CameraScanBatch()

                cameraFlow =
                    CameraFlow.INCOMING_RETURN

                currentScreen =
                    AppScreen.CAMERA
            },

            onOutgoingReturnClick = {

                quickActionVisible =
                    false

                cameraBatch =
                    CameraScanBatch()

                cameraFlow =
                    CameraFlow.OUTGOING_RETURN

                currentScreen =
                    AppScreen.CAMERA
            }
        )

        /*
         * =========================================================
         * GLOBAL ALT MENÜ
         *
         * ÜRÜNLER EKRANINDA GÖSTERİLMEZ.
         *
         * Çünkü ProductsScreen kendi alt menüsünü
         * zaten gösteriyor.
         * =========================================================
         */

        if (currentScreen != AppScreen.PRODUCTS) {

            BottomNavigationBar(

                modifier =
                    Modifier.align(
                        Alignment.BottomCenter
                    ),

                selectedItem = when (currentScreen) {

                    AppScreen.HOME ->
                        BottomNavigationItemType.HOME

                    AppScreen.PRODUCTS,
                    AppScreen.ADD_PRODUCT,
                    AppScreen.CAMERA ->
                        BottomNavigationItemType.PRODUCTS

                    AppScreen.RETURNS,
                    AppScreen.INCOMING_RETURN,
                    AppScreen.INCOMING_RETURN_REVIEW,
                    AppScreen.OUTGOING_RETURN ->
                        BottomNavigationItemType.RETURNS

                    AppScreen.SETTINGS ->
                        BottomNavigationItemType.SETTINGS

                    AppScreen.WAREHOUSE,
                    AppScreen.WAREHOUSE_ROW,
                    AppScreen.WAREHOUSE_RACK,
                    AppScreen.WAREHOUSE_LOCATION,
                    AppScreen.LISTS ->
                        BottomNavigationItemType.HOME
                },

                onHomeClick = {

                    quickActionVisible =
                        false

                    currentScreen =
                        AppScreen.HOME
                },

                onProductsClick = {

                    quickActionVisible =
                        false

                    currentScreen =
                        AppScreen.PRODUCTS
                },

                onQuickActionClick = {

                    quickActionVisible =
                        !quickActionVisible
                },

                onReturnsClick = {

                    quickActionVisible =
                        false

                    currentScreen =
                        AppScreen.RETURNS
                },

                onSettingsClick = {

                    quickActionVisible =
                        false

                    currentScreen =
                        AppScreen.SETTINGS
                }
            )
        }
    }
}