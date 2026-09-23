package com.example.depotakipai.ui.navigation

import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow

data class WarehouseNavigationState(
    val selectedRow: WarehouseRow? = null,
    val selectedRack: WarehouseRack? = null
) {

    fun selectRow(
        row: WarehouseRow
    ): WarehouseNavigationState {
        return copy(
            selectedRow = row,
            selectedRack = null
        )
    }

    fun selectRack(
        rack: WarehouseRack
    ): WarehouseNavigationState {
        return copy(
            selectedRack = rack
        )
    }

    fun clearSelection(): WarehouseNavigationState {
        return copy(
            selectedRow = null,
            selectedRack = null
        )
    }

    fun clearRackSelection(): WarehouseNavigationState {
        return copy(
            selectedRack = null
        )
    }
}