package com.example.depotakipai.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow
import com.example.depotakipai.ui.warehouse.WarehouseRowScreen
import com.example.depotakipai.ui.warehouse.WarehouseViewModel
import com.example.depotakipai.ui.warehouse.WarehouseViewModelFactory

@Composable
fun WarehouseRowNavigation(
    row: WarehouseRow,
    onBack: () -> Unit = {},
    onRackClick: (WarehouseRack) -> Unit = {},
    onAddRackClick: () -> Unit = {}
) {

    val context = LocalContext.current

    val application = remember {
        context.applicationContext as Application
    }

    val viewModel: WarehouseViewModel = viewModel(
        factory = WarehouseViewModelFactory(
            application = application
        )
    )

    val racks = viewModel.getRacksForRow(
        rowId = row.id
    )

    WarehouseRowScreen(
        row = row,
        racks = racks,
        isLoading = false,
        onBack = onBack,
        onRackClick = onRackClick,
        onAddRackClick = onAddRackClick
    )
}