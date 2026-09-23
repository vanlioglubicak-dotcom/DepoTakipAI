package com.example.depotakipai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depotakipai.ui.warehouse.WarehouseScreen
import com.example.depotakipai.ui.warehouse.WarehouseViewModel
import com.example.depotakipai.ui.warehouse.WarehouseViewModelFactory

@Composable
fun WarehouseNavigation(
    onBack: () -> Unit = {},
    onListsClick: () -> Unit = {},
    onProductsClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onStockClick: () -> Unit = {}
) {

    val context = LocalContext.current

    val application = remember {
        context.applicationContext as android.app.Application
    }

    val viewModel: WarehouseViewModel = viewModel(
        factory = WarehouseViewModelFactory(
            application = application
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WarehouseScreen(
        rows = uiState.rows,

        onBack = onBack,

        onListsClick = onListsClick,

        onProductsClick = onProductsClick,

        onLocationClick = onLocationClick,

        onStockClick = onStockClick,

        onRowClick = { row ->
            // Sıra detay ekranı sonraki adımda bağlanacak.
        },

        onAddRowClick = {
            // Dinamik sıra ekleme ekranı sonraki adımda bağlanacak.
        },

        onSearchClick = { query ->
            // Depo içi ürün/konum araması sonraki adımda bağlanacak.
        }
    )
}