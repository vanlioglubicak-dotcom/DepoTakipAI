package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListType

enum class InventoryStockMovementDirection {
    INCREASE,
    DECREASE,
    NONE
}

class GetInventoryStockMovementDirectionUseCase {

    operator fun invoke(
        type: InventoryListType
    ): InventoryStockMovementDirection {

        return when (type) {

            InventoryListType.ANA_LISTE ->
                InventoryStockMovementDirection.NONE

            InventoryListType.YENI_GELEN ->
                InventoryStockMovementDirection.INCREASE

            InventoryListType.GIDEN ->
                InventoryStockMovementDirection.DECREASE

            InventoryListType.GIDEN_IADE ->
                InventoryStockMovementDirection.DECREASE

            InventoryListType.GELEN_IADE ->
                InventoryStockMovementDirection.INCREASE
        }
    }
}