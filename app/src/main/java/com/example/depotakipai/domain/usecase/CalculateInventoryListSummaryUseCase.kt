package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListItem

data class InventoryListSummary(
    val productCount: Int,
    val totalQuantity: Int
)

class CalculateInventoryListSummaryUseCase {

    operator fun invoke(
        items: List<InventoryListItem>
    ): InventoryListSummary {

        val productCount = items
            .map { it.productCode }
            .distinct()
            .size

        val totalQuantity = items.sumOf {
            it.quantity
        }

        return InventoryListSummary(
            productCount = productCount,
            totalQuantity = totalQuantity
        )
    }
}