package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class PrepareInventoryListUseCase(
    private val validateInventoryList: ValidateInventoryListUseCase,
    private val calculateSummary: CalculateInventoryListSummaryUseCase
) {

    operator fun invoke(
        inventoryList: InventoryList
    ): InventoryList? {

        // Liste ürünleri bulunmalıdır.
        if (inventoryList.items.isEmpty()) {
            return null
        }

        // Liste bütün kurallara uygun olmalıdır.
        if (!validateInventoryList(inventoryList)) {
            return null
        }

        // Ürün sayısı ve toplam adet yeniden hesaplanır.
        val summary = calculateSummary(inventoryList.items)

        return inventoryList.copy(
            productCount = summary.productCount,
            totalQuantity = summary.totalQuantity
        )
    }
}