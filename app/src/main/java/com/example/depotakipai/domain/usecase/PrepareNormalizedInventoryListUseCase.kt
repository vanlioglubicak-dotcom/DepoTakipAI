package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class PrepareNormalizedInventoryListUseCase(
    private val normalizeItems: NormalizeInventoryListItemsUseCase,
    private val calculateSummary: CalculateInventoryListSummaryUseCase
) {

    operator fun invoke(
        inventoryList: InventoryList
    ): InventoryList? {

        val normalizedItems = normalizeItems(
            inventoryList.items
        )

        if (normalizedItems.isEmpty()) {
            return null
        }

        val summary = calculateSummary(
            normalizedItems
        )

        return inventoryList.copy(
            productCount = summary.productCount,
            totalQuantity = summary.totalQuantity,
            items = normalizedItems
        )
    }
}