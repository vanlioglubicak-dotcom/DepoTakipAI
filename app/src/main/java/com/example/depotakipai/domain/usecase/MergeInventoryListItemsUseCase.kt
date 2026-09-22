package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListItem

class MergeInventoryListItemsUseCase {

    operator fun invoke(
        items: List<InventoryListItem>
    ): List<InventoryListItem> {

        if (items.isEmpty()) {
            return emptyList()
        }

        return items
            .groupBy {
                MergeKey(
                    productCode = it.productCode.trim().uppercase(),
                    color = it.color.trim().uppercase(),
                    size = it.size.trim().uppercase()
                )
            }
            .map { (key, groupedItems) ->

                val firstItem = groupedItems.first()

                firstItem.copy(
                    productCode = key.productCode,
                    color = key.color,
                    size = key.size,
                    quantity = groupedItems.sumOf { it.quantity }
                )
            }
            .sortedWith(
                compareBy(
                    { it.productCode },
                    { it.color },
                    { it.size }
                )
            )
    }

    private data class MergeKey(
        val productCode: String,
        val color: String,
        val size: String
    )
}