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
import com.example.depotakipai.ui.components.BottomNavigationBar
import com.example.depotakipai.ui.components.BottomNavigationItemType
import com.example.depotakipai.ui.components.QuickActionMenu
import com.example.depotakipai.ui.home.HomeScreen
import com.example.depotakipai.ui.lists.DepotListDetailScreen
import com.example.depotakipai.ui.lists.DepotListViewModel
import com.example.depotakipai.ui.lists.DepotListViewModelFactory
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

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val depotListRepository = remember(database) {
        DepotListRecordRepository(
            dao = database.depotListRecordDao()
        )
    }

    val getDepotListRecordsUseCase = remember(depotListRepository) {
        GetDepotListRecordsUseCase(
            repository = depotListRepository
        )
    }

    val saveDepotListRecordUseCase = remember(depotListRepository) {
        SaveDepotListRecordUseCase(
            repository = depotListRepository
        )
    }

    val depotListViewModel: DepotListViewModel =
        viewModel(
            factory = remember(
                getDepotListRecordsUseCase,
                saveDepotListRecordUseCase
            ) {
                DepotListViewModelFactory(
                    getDepotListRecordsUseCase =
                        getDepotListRecordsUseCase,
                    saveDepotListRecordUseCase =
                        saveDepotListRecordUseCase
                )
            }
        )

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
                    AppScreen.HOME

                AppScreen.LIST_DETAIL ->
                    AppScreen.LISTS

                AppScreen.SETTINGS ->
                    AppScreen.HOME

                AppScreen.HOME ->
                    AppScreen.HOME
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (currentScreen) {

            AppScreen.HOME -> {

                HomeScreen(
                    onProductsClick = {
                        quickActionVisible = false
                        currentScreen = AppScreen.PRODUCTS
                    },

                    onAddProductClick = {
                        quickActionVisible = false
                        cameraBatch = CameraScanBatch()
                        cameraFlow = CameraFlow.NEW_PRODUCT
                        currentScreen = AppScreen.CAMERA
                    },

                    onReturnsClick = {
                        quickActionVisible = false
                        currentScreen = AppScreen.RETURNS
                    },

                    onSettingsClick = {
                        quickActionVisible = false
                        currentScreen = AppScreen.SETTINGS
                    },

                    onWarehouseClick = {
                        quickActionVisible = false
                        currentScreen = AppScreen.WAREHOUSE
                    },

                    onListsClick = {
                        quickActionVisible = false
                        currentScreen = AppScreen.LISTS
                    },

                    onIncomingReturnClick = {
                        quickActionVisible = false
                        cameraBatch = CameraScanBatch()
                        cameraFlow = CameraFlow.INCOMING_RETURN
                        currentScreen = AppScreen.CAMERA
                    },

                    onOutgoingReturnClick = {
                        quickActionVisible = false
                        cameraBatch = CameraScanBatch()
                        cameraFlow = CameraFlow.OUTGOING_RETURN
                        currentScreen = AppScreen.CAMERA
                    }
                )
            }

            AppScreen.PRODUCTS -> {

                ProductsScreen(
                    onBack = {
                        currentScreen = AppScreen.HOME
                    },

                    onAddProduct = {
                        cameraBatch = CameraScanBatch()
                        cameraFlow = CameraFlow.NEW_PRODUCT
                        currentScreen = AppScreen.CAMERA
                    },

                    onReturnsClick = {
                        currentScreen = AppScreen.RETURNS
                    },

                    onSettingsClick = {
                        currentScreen = AppScreen.SETTINGS
                    }
                )
            }

            AppScreen.ADD_PRODUCT -> {

                AddProductScreen(
                    onProductSaved = {
                        cameraBatch = CameraScanBatch()
                        cameraFlow = CameraFlow.NONE
                        currentScreen = AppScreen.PRODUCTS
                    },

                    onBack = {
                        cameraBatch = CameraScanBatch()
                        cameraFlow = CameraFlow.NONE
                        currentScreen = AppScreen.PRODUCTS
                    },

                    onCameraClick = {
                        cameraBatch = CameraScanBatch()
                        cameraFlow = CameraFlow.NEW_PRODUCT
                        currentScreen = AppScreen.CAMERA
                    },

                    cameraResult = null
                )
            }

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

                        /*
                         * =================================================
                         * KAMERA TARAMASI TAMAMLANDI
                         * =================================================
                         */

                        cameraBatch = batch

                        /*
                         * =================================================
                         * KAMERA ONAYLANAN ÜRÜNLERİ LİSTELERE KAYDET
                         * =================================================
                         */

                        if (batch.items.isNotEmpty()) {

                            /*
                             * Kamera akışına göre ana hareket listesi.
                             */
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

                            /*
                             * =============================================
                             * OKUNANLAR
                             * =============================================
                             *
                             * Kamera ile ONAYLANAN her ürün burada tutulur.
                             */

                            val readRecords =
                                batch.items.map { item ->

                                    DepotListRecord(
                                        category =
                                            DepotListCategory.OKUNANLAR,

                                        productCode =
                                            item.productCode,

                                        color =
                                            item.color,

                                        size =
                                            item.size,

                                        quantity = 1,

                                        createdAt =
                                            item.scannedAt
                                    )
                                }

                            depotListViewModel.saveRecords(
                                readRecords
                            )

                            /*
                             * =============================================
                             * HAREKET LİSTESİ
                             * =============================================
                             *
                             * Yeni ürün / Gelen İade / Giden İade
                             * kendi listesine de kaydedilir.
                             */

                            if (
                                targetCategory !=
                                DepotListCategory.OKUNANLAR
                            ) {

                                val movementRecords =
                                    batch.items.map { item ->

                                        DepotListRecord(
                                            category =
                                                targetCategory,

                                            productCode =
                                                item.productCode,

                                            color =
                                                item.color,

                                            size =
                                                item.size,

                                            quantity = 1,

                                            createdAt =
                                                item.scannedAt
                                        )
                                    }

                                depotListViewModel.saveRecords(
                                    movementRecords
                                )
                            }
                        }

                        /*
                         * Eski ekran akışı aynen devam eder.
                         */

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

            AppScreen.RETURNS -> {

                ReturnsScreen(
                    onBack = {
                        currentScreen = AppScreen.HOME
                    },

                    onIncomingReturnClick = {
                        cameraBatch = CameraScanBatch()
                        cameraFlow = CameraFlow.INCOMING_RETURN
                        currentScreen = AppScreen.CAMERA
                    },

                    onOutgoingReturnClick = {
                        cameraBatch = CameraScanBatch()
                        cameraFlow = CameraFlow.OUTGOING_RETURN
                        currentScreen = AppScreen.CAMERA
                    }
                )
            }

            AppScreen.INCOMING_RETURN -> {

                IncomingReturnScreen(
                    database = database,
                    onBack = {
                        currentScreen = AppScreen.RETURNS
                    }
                )
            }

            AppScreen.INCOMING_RETURN_REVIEW -> {

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
                            // Mevcut düzenleme akışı korunuyor.
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

            AppScreen.OUTGOING_RETURN -> {

                OutgoingReturnScreen(
                    database = database,
                    onBack = {
                        currentScreen =
                            AppScreen.RETURNS
                    }
                )
            }

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

                        selectedRack = rack
                        selectedLocation = null

                        currentScreen =
                            AppScreen.WAREHOUSE_RACK
                    },

                    onAddRackClick = {
                    }
                )
            }

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

            AppScreen.LIST_DETAIL -> {

                val category =
                    selectedListCategory

                if (category == null) {

                    currentScreen =
                        AppScreen.LISTS

                } else {

                    DepotListDetailScreen(

                        category = category,

                        viewModel =
                            depotListViewModel,

                        onBack = {
                            currentScreen =
                                AppScreen.LISTS
                        }
                    )
                }
            }

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
                    }
                )
            }
        }

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

        if (currentScreen != AppScreen.PRODUCTS) {

            BottomNavigationBar(

                modifier =
                    Modifier.align(
                        Alignment.BottomCenter
                    ),

                selectedItem =
                    when (currentScreen) {

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
                        AppScreen.LISTS,
                        AppScreen.LIST_DETAIL ->
                            BottomNavigationItemType.HOME
                    },

                onHomeClick = {
                    quickActionVisible = false
                    currentScreen = AppScreen.HOME
                },

                onProductsClick = {
                    quickActionVisible = false
                    currentScreen = AppScreen.PRODUCTS
                },

                onQuickActionClick = {
                    quickActionVisible =
                        !quickActionVisible
                },

                onReturnsClick = {
                    quickActionVisible = false
                    currentScreen = AppScreen.RETURNS
                },

                onSettingsClick = {
                    quickActionVisible = false
                    currentScreen = AppScreen.SETTINGS
                }
            )
        }
    }
}