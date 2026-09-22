package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListItem

class NormalizeInventoryListItemsUseCase(
    private val normalizeItem: NormalizeInventoryListItemUseCase,
    private val mergeItems: MergeInventoryListItemsUseCase
) {

    operator fun invoke(
        items: List<InventoryListItem>
    ): List<InventoryListItem> {

        if (items.isEmpty()) {
            return emptyList()
        }

        val normalizedItems = items.mapNotNull { item ->
            normalizeItem(item)
        }

        if (normalizedItems.isEmpty()) {
            return emptyList()
        }

        return mergeItems(normalizedItems)
    }
}