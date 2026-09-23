package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

data class InventoryStockMovement(
    val listId: Long,
    val type: String,
    val productCode: String,
    val color: String,
    val size: String,
    val quantity: Int,
    val destination: String?
)

class PrepareInventoryStockMovementUseCase {

    operator fun invoke(
        inventoryList: InventoryList
    ): List<InventoryStockMovement> {

        if (!inventoryList.isApproved) {
            return emptyList()
        }

        return inventoryList.items
            .filter { it.quantity > 0 }
            .map { item ->
                InventoryStockMovement(
                    listId = inventoryList.id,
                    type = inventoryList.type.name,
                    productCode = item.productCode,
                    color = item.color,
                    size = item.size,
                    quantity = item.quantity,
                    destination = inventoryList.destination
                )
            }
    }
}