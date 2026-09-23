package com.example.depotakipai.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.ui.warehouse.WarehouseLocationPanel
import com.example.depotakipai.ui.warehouse.WarehouseViewModel
import com.example.depotakipai.ui.warehouse.WarehouseViewModelFactory

@Composable
fun WarehouseRackNavigation(
    rack: WarehouseRack,
    onBack: () -> Unit = {},
    onLocationClick: (WarehouseLocation) -> Unit = {},
    onAddLocationClick: () -> Unit = {}
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

    var locations by remember(rack.id) {
        mutableStateOf<List<WarehouseLocation>>(emptyList())
    }

    LaunchedEffect(rack.id) {

        viewModel.getLocationsForRack(
            rackId = rack.id
        ) { result ->

            locations = result
        }
    }

    WarehouseLocationPanel(
        rack = rack,
        locations = locations,
        onLocationClick = onLocationClick,
        onAddLocationClick = onAddLocationClick
    )
}