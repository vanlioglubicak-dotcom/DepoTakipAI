package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class CreateInventoryStockMovementsUseCase(
    private val createMovement: CreateInventoryStockMovementUseCase
) {

    operator fun invoke(
        inventoryList: InventoryList
    ): List<PreparedInventoryStockMovement> {

        if (!inventoryList.isApproved) {
            return emptyList()
        }

        return inventoryList.items.mapNotNull { item ->
            createMovement(
                inventoryList = inventoryList,
                item = item
            )
        }
    }
}