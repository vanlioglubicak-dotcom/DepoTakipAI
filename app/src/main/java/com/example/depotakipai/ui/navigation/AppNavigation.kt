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
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.depotakipai.data.local.DatabaseProvider
import com.example.depotakipai.data.repository.DepotListRecordRepository

import com.example.depotakipai.domain.model.CameraScanBatch
import com.example.depotakipai.domain.model.DepotListCategory
import com.example.depotakipai.domain.model.DepotListRecord
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult
import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow

import com.example.depotakipai.domain.usecase.GetDepotListRecordsUseCase
import com.example.depotakipai.domain.usecase.SaveDepotListRecordUseCase

import com.example.depotakipai.ui.camera.ContinuousCameraScanScreen

import com.example.depotakipai.ui.catalog.CatalogAddScreen
import com.example.depotakipai.ui.catalog.CatalogScreen

import com.example.depotakipai.ui.components.BottomNavigationBar
import com.example.depotakipai.ui.components.BottomNavigationItemType
import com.example.depotakipai.ui.components.QuickActionMenu

import com.example.depotakipai.ui.home.HomeScreen

import com.example.depotakipai.ui.lists.DepotListDetailScreen
import com.example.depotakipai.ui.lists.DepotListViewModel
import com.example.depotakipai.ui.lists.DepotListViewModelFactory
import com.example.depotakipai.ui.lists.ListCategory
import com.example.depotakipai.ui.lists.ListsScreen
import com.example.depotakipai.ui.lists.PendingScanListScreen

import com.example.depotakipai.ui.products.ProductsScreen

import com.example.depotakipai.ui.returns.IncomingReturnReviewScreen
import com.example.depotakipai.ui.returns.IncomingReturnScreen
import com.example.depotakipai.ui.returns.OutgoingReturnScreen
import com.example.depotakipai.ui.returns.ReturnsScreen

import com.example.depotakipai.ui.warehouse.WarehouseScreen


private enum class AppScreen {

    HOME,

    PRODUCTS,

    CATALOG,
    CATALOG_ADD,

    CAMERA,
    PENDING_SCAN_LIST,

    RETURNS,
    INCOMING_RETURN,
    OUTGOING_RETURN,
    INCOMING_RETURN_REVIEW,

    WAREHOUSE,
    WAREHOUSE_ROW,
    WAREHOUSE_RACK,
    WAREHOUSE_LOCATION,

    LISTS,
    LIST_DETAIL,

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

    var cameraFlow by remember {
        mutableStateOf(CameraFlow.NONE)
    }

    var cameraBatch by remember {
        mutableStateOf(CameraScanBatch())
    }

    var incomingReturnAnalysisResult by remember {
        mutableStateOf<IncomingReturnAnalysisResult?>(null)
    }

    var selectedListCategory by remember {
        mutableStateOf<DepotListCategory?>(null)
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


    val database = remember {
        DatabaseProvider.getDatabase(context)
    }


    val depotListRepository = remember(database) {

        DepotListRecordRepository(
            dao = database.depotListRecordDao()
        )
    }


    val getDepotListRecordsUseCase =
        remember(depotListRepository) {

            GetDepotListRecordsUseCase(
                repository = depotListRepository
            )
        }


    val saveDepotListRecordUseCase =
        remember(depotListRepository) {

            SaveDepotListRecordUseCase(
                repository = depotListRepository
            )
        }


    val depotListViewModel: DepotListViewModel =
        viewModel(

            factory = remember(
                getDepotListRecordsUseCase,
                saveDepotListRecordUseCase,
                depotListRepository
            ) {

                DepotListViewModelFactory(

                    getDepotListRecordsUseCase =
                        getDepotListRecordsUseCase,

                    saveDepotListRecordUseCase =
                        saveDepotListRecordUseCase,

                    repository =
                        depotListRepository
                )
            }
        )


    BackHandler(

        enabled =
            currentScreen != AppScreen.HOME ||
                    quickActionVisible

    ) {

        if (quickActionVisible) {

            quickActionVisible = false

        } else {

            currentScreen =

                when (currentScreen) {

                    AppScreen.HOME ->
                        AppScreen.HOME


                    AppScreen.PRODUCTS ->
                        AppScreen.HOME


                    AppScreen.CATALOG ->
                        AppScreen.HOME


                    AppScreen.CATALOG_ADD ->
                        AppScreen.CATALOG


                    AppScreen.CAMERA -> {

                        when (cameraFlow) {

                            CameraFlow.NEW_PRODUCT,
                            CameraFlow.INCOMING_RETURN,
                            CameraFlow.OUTGOING_RETURN ->

                                AppScreen.PENDING_SCAN_LIST


                            CameraFlow.NONE ->
                                AppScreen.HOME
                        }
                    }


                    AppScreen.PENDING_SCAN_LIST ->
                        AppScreen.HOME


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
                        AppScreen.HOME


                    AppScreen.LIST_DETAIL ->
                        AppScreen.LISTS


                    AppScreen.SETTINGS ->
                        AppScreen.HOME
                }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {


        when (currentScreen) {


            // =========================================================
            // HOME
            // =========================================================

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


                    onListsClick = {

                        quickActionVisible = false

                        currentScreen =
                            AppScreen.LISTS
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
                    },


                    onCatalogClick = {

                        quickActionVisible = false

                        currentScreen =
                            AppScreen.CATALOG
                    }
                )
            }


            // =========================================================
            // PRODUCTS
            // =========================================================

            AppScreen.PRODUCTS -> {

                ProductsScreen(

                    onBack = {

                        quickActionVisible = false

                        currentScreen =
                            AppScreen.HOME
                    },


                    onAddProduct = {

                        /*
                         * ÖNEMLİ:
                         *
                         * Eski AddProductScreen artık açılmıyor.
                         *
                         * Ürünler ekranındaki + yalnızca
                         * hızlı işlem menüsünü açar.
                         */

                        quickActionVisible = true
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
                    }
                )
            }


            // =========================================================
            // CATALOG
            // =========================================================

            AppScreen.CATALOG -> {

                CatalogScreen(

                    onBack = {

                        quickActionVisible = false

                        currentScreen =
                            AppScreen.HOME
                    },


                    onAddCatalogItem = {

                        /*
                         * KATALOG + BASILDIĞINDA:
                         *
                         * Eski barkod kamera açılmaz.
                         *
                         * Doğrudan katalog ürün bilgi ekranı açılır.
                         */

                        quickActionVisible = false

                        currentScreen =
                            AppScreen.CATALOG_ADD
                    },


                    items = emptyList()
                )
            }


            // =========================================================
            // CATALOG ADD
            // =========================================================

            AppScreen.CATALOG_ADD -> {

                CatalogAddScreen(

                    onBack = {

                        currentScreen =
                            AppScreen.CATALOG
                    },


                    onSave = { _, _, _, _, _ ->

                        currentScreen =
                            AppScreen.CATALOG
                    }
                )
            }


            // =========================================================
            // CAMERA
            // =========================================================

            AppScreen.CAMERA -> {

                ContinuousCameraScanScreen(

                    onBack = {

                        currentScreen =

                            when (cameraFlow) {

                                CameraFlow.NEW_PRODUCT,
                                CameraFlow.INCOMING_RETURN,
                                CameraFlow.OUTGOING_RETURN ->

                                    AppScreen.PENDING_SCAN_LIST


                                CameraFlow.NONE ->
                                    AppScreen.HOME
                            }
                    },


                    onFinished = { batch ->

                        cameraBatch =
                            batch

                        currentScreen =
                            AppScreen.PENDING_SCAN_LIST
                    }
                )
            }


            // =========================================================
            // PENDING SCAN LIST
            // =========================================================

            AppScreen.PENDING_SCAN_LIST -> {

                val title =

                    when (cameraFlow) {

                        CameraFlow.NEW_PRODUCT ->
                            "YENİ ÜRÜNLER"


                        CameraFlow.INCOMING_RETURN ->
                            "GELEN İADE"


                        CameraFlow.OUTGOING_RETURN ->
                            "GİDEN İADE"


                        CameraFlow.NONE ->
                            "OKUNANLAR"
                    }


                PendingScanListScreen(

                    title = title,

                    initialBatch =
                        cameraBatch,


                    onBack = {

                        cameraBatch =
                            CameraScanBatch()

                        cameraFlow =
                            CameraFlow.NONE

                        currentScreen =
                            AppScreen.HOME
                    },


                    onConfirm = { confirmedBatch ->

                        cameraBatch =
                            confirmedBatch


                        if (
                            confirmedBatch.items.isNotEmpty()
                        ) {

                            val targetCategory =

                                when (cameraFlow) {

                                    CameraFlow.NEW_PRODUCT ->
                                        DepotListCategory.YENI_URUNLER


                                    CameraFlow.INCOMING_RETURN ->
                                        DepotListCategory.GELEN_IADE


                                    CameraFlow.OUTGOING_RETURN ->
                                        DepotListCategory.GIDEN_IADE


                                    CameraFlow.NONE ->
                                        DepotListCategory.OKUNANLAR
                                }


                            val records =
                                confirmedBatch.items.map { item ->

                                    DepotListRecord(

                                        category =
                                            targetCategory,

                                        productCode =
                                            item.productCode,

                                        color =
                                            item.color,

                                        size =
                                            item.size,

                                        quantity =
                                            1,

                                        createdAt =
                                            item.scannedAt
                                    )
                                }


                            depotListViewModel
                                .saveRecords(records)
                        }


                        cameraBatch =
                            CameraScanBatch()


                        val finishedFlow =
                            cameraFlow


                        cameraFlow =
                            CameraFlow.NONE


                        currentScreen =

                            when (finishedFlow) {

                                CameraFlow.NEW_PRODUCT,
                                CameraFlow.INCOMING_RETURN,
                                CameraFlow.OUTGOING_RETURN ->
                                    AppScreen.LISTS


                                CameraFlow.NONE ->
                                    AppScreen.LISTS
                            }
                    }
                )
            }


            // =========================================================
            // RETURNS
            // =========================================================

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


            // =========================================================
            // INCOMING RETURN
            // =========================================================

            AppScreen.INCOMING_RETURN -> {

                IncomingReturnScreen(

                    database =
                        database,


                    onBack = {

                        currentScreen =
                            AppScreen.RETURNS
                    }
                )
            }


            // =========================================================
            // INCOMING RETURN REVIEW
            // =========================================================

            AppScreen.INCOMING_RETURN_REVIEW -> {

                val result =
                    incomingReturnAnalysisResult


                if (result == null) {

                    IncomingReturnScreen(

                        database =
                            database,


                        onBack = {

                            currentScreen =
                                AppScreen.INCOMING_RETURN
                        }
                    )

                } else {

                    IncomingReturnReviewScreen(

                        database =
                            database,

                        result =
                            result,


                        onBack = {

                            currentScreen =
                                AppScreen.INCOMING_RETURN
                        },


                        onEdit = {},


                        onConfirmSuccess = {

                            incomingReturnAnalysisResult =
                                null

                            currentScreen =
                                AppScreen.INCOMING_RETURN
                        }
                    )
                }
            }


            // =========================================================
            // OUTGOING RETURN
            // =========================================================

            AppScreen.OUTGOING_RETURN -> {

                OutgoingReturnScreen(

                    database =
                        database,


                    onBack = {

                        currentScreen =
                            AppScreen.RETURNS
                    }
                )
            }


            // =========================================================
            // WAREHOUSE
            // =========================================================

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


                    onLocationClick = {},


                    onStockClick = {},


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


                    onAddRowClick = {},


                    onSearchClick = {}
                )
            }


            // =========================================================
            // WAREHOUSE ROW
            // =========================================================

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


                    onAddRackClick = {}
                )
            }


            // =========================================================
            // WAREHOUSE RACK
            // =========================================================

            AppScreen.WAREHOUSE_RACK -> {

                val rack =
                    selectedRack


                if (rack == null) {

                    currentScreen =
                        AppScreen.WAREHOUSE_ROW

                } else {

                    WarehouseRackNavigation(

                        rack =
                            rack,


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


                        onAddLocationClick = {}
                    )
                }
            }


            // =========================================================
            // WAREHOUSE LOCATION
            // =========================================================

            AppScreen.WAREHOUSE_LOCATION -> {

                val location =
                    selectedLocation


                if (location == null) {

                    currentScreen =
                        AppScreen.WAREHOUSE_RACK

                } else {

                    WarehouseLocationNavigation(

                        location =
                            location,


                        onBack = {

                            currentScreen =
                                AppScreen.WAREHOUSE_RACK
                        },


                        onProductClick = {},


                        onEditClick = {}
                    )
                }
            }


            // =========================================================
            // LISTS
            // =========================================================

            AppScreen.LISTS -> {

                ListsScreen(

                    onBack = {

                        currentScreen =
                            AppScreen.HOME
                    },


                    onCategoryClick = { category ->

                        selectedListCategory =

                            when (category) {

                                ListCategory.ANA_LISTE ->
                                    DepotListCategory.OKUNANLAR


                                ListCategory.YENI_GELEN ->
                                    DepotListCategory.YENI_URUNLER


                                ListCategory.GIDEN ->
                                    DepotListCategory.GIDEN


                                ListCategory.GIDEN_IADE ->
                                    DepotListCategory.GIDEN_IADE


                                ListCategory.GELEN_IADE ->
                                    DepotListCategory.GELEN_IADE
                            }


                        currentScreen =
                            AppScreen.LIST_DETAIL
                    }
                )
            }


            // =========================================================
            // LIST DETAIL
            // =========================================================

            AppScreen.LIST_DETAIL -> {

                val category =
                    selectedListCategory


                if (category == null) {

                    currentScreen =
                        AppScreen.LISTS

                } else {

                    DepotListDetailScreen(

                        category =
                            category,

                        viewModel =
                            depotListViewModel,


                        onBack = {

                            currentScreen =
                                AppScreen.LISTS
                        }
                    )
                }
            }


            // =========================================================
            // SETTINGS
            // =========================================================

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
                    },


                    onListsClick = {

                        currentScreen =
                            AppScreen.LISTS
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
                    },


                    onCatalogClick = {

                        currentScreen =
                            AppScreen.CATALOG
                    }
                )
            }
        }


        // =============================================================
        // QUICK ACTION MENU
        // =============================================================

        QuickActionMenu(

            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                ),


            visible =
                quickActionVisible,


            onNewProductClick = {

                quickActionVisible = false

                cameraBatch =
                    CameraScanBatch()

                cameraFlow =
                    CameraFlow.NEW_PRODUCT

                currentScreen =
                    AppScreen.CAMERA
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


        // =============================================================
        // BOTTOM NAVIGATION
        // =============================================================

        if (
            currentScreen != AppScreen.PRODUCTS &&
            currentScreen != AppScreen.PENDING_SCAN_LIST
        ) {

            BottomNavigationBar(

                modifier =
                    Modifier.align(
                        Alignment.BottomCenter
                    ),


                selectedItem =

                    when (currentScreen) {

                        AppScreen.HOME ->
                            BottomNavigationItemType.HOME


                        AppScreen.PRODUCTS ->
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
                        AppScreen.LISTS,
                        AppScreen.LIST_DETAIL,
                        AppScreen.CATALOG,
                        AppScreen.CATALOG_ADD ->
                            BottomNavigationItemType.HOME


                        AppScreen.CAMERA,
                        AppScreen.PENDING_SCAN_LIST ->
                            BottomNavigationItemType.PRODUCTS
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

                    when (currentScreen) {

                        /*
                         * ÜRÜNLER +:
                         * Yalnızca hızlı işlem menüsü.
                         */
                        AppScreen.PRODUCTS -> {

                            quickActionVisible =
                                !quickActionVisible
                        }


                        /*
                         * KATALOG +:
                         * Doğrudan katalog ürün bilgi ekranı.
                         */
                        AppScreen.CATALOG -> {

                            quickActionVisible =
                                false

                            currentScreen =
                                AppScreen.CATALOG_ADD
                        }


                        /*
                         * Diğer ekranlar:
                         * Mevcut hızlı işlem menüsü.
                         */
                        else -> {

                            quickActionVisible =
                                !quickActionVisible
                        }
                    }
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