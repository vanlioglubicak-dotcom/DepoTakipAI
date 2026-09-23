package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListItem

class CalculateInventoryStockMovementQuantityUseCase {

    operator fun invoke(
        item: InventoryListItem
    ): Int {

        if (item.quantity <= 0) {
            return 0
        }

        return item.quantity
    }
}