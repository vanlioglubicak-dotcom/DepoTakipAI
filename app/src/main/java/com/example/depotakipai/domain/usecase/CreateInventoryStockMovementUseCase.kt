package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.model.InventoryListItem

data class PreparedInventoryStockMovement(
    val listId: Long,
    val type: String,
    val direction: InventoryStockMovementDirection,
    val productCode: String,
    val color: String,
    val size: String,
    val quantity: Int,
    val destination: String?
)

class CreateInventoryStockMovementUseCase(
    private val getMovementDirection: GetInventoryStockMovementDirectionUseCase,
    private val calculateQuantity: CalculateInventoryStockMovementQuantityUseCase
) {

    operator fun invoke(
        inventoryList: InventoryList,
        item: InventoryListItem
    ): PreparedInventoryStockMovement? {

        // Sadece onaylanmış listeler stok hareketi oluşturabilir.
        if (!inventoryList.isApproved) {
            return null
        }

        val direction = getMovementDirection(
            inventoryList.type
        )

        // Ana Liste doğrudan stok hareketi oluşturmaz.
        if (direction == InventoryStockMovementDirection.NONE) {
            return null
        }

        val quantity = calculateQuantity(item)

        if (quantity <= 0) {
            return null
        }

        return PreparedInventoryStockMovement(
            listId = inventoryList.id,
            type = inventoryList.type.name,
            direction = direction,
            productCode = item.productCode,
            color = item.color,
            size = item.size,
            quantity = quantity,
            destination = inventoryList.destination
        )
    }
}