package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class PrepareCompleteInventoryListUseCase(
    private val prepareItems: PrepareInventoryListItemsUseCase,
    private val calculateSummary: CalculateInventoryListSummaryUseCase
) {

    operator fun invoke(
        inventoryList: InventoryList
    ): InventoryList? {

        val preparedItems = prepareItems(
            inventoryList.items
        )

        if (preparedItems.isEmpty()) {
            return null
        }

        val summary = calculateSummary(
            preparedItems
        )

        return inventoryList.copy(
            productCount = summary.productCount,
            totalQuantity = summary.totalQuantity,
            items = preparedItems
        )
    }
}