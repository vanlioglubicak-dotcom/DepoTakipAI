package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListItem

class SortInventoryListItemsUseCase {

    operator fun invoke(
        items: List<InventoryListItem>
    ): List<InventoryListItem> {

        return items.sortedWith(
            compareBy(
                { it.productCode.uppercase() },
                { it.color.uppercase() },
                { sizeOrder(it.size) }
            )
        )
    }

    private fun sizeOrder(
        size: String
    ): Int {

        return when (size.trim().uppercase()) {
            "XXS" -> 0
            "XS" -> 1
            "S" -> 2
            "M" -> 3
            "L" -> 4
            "XL" -> 5
            "XXL" -> 6
            "3XL" -> 7
            "4XL" -> 8
            "5XL" -> 9
            else -> 100
        }
    }
}