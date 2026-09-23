package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListItem

class PrepareInventoryListItemsUseCase(
    private val normalizeItems: NormalizeInventoryListItemsUseCase,
    private val sortItems: SortInventoryListItemsUseCase
) {

    operator fun invoke(
        items: List<InventoryListItem>
    ): List<InventoryListItem> {

        if (items.isEmpty()) {
            return emptyList()
        }

        val normalizedItems = normalizeItems(items)

        if (normalizedItems.isEmpty()) {
            return emptyList()
        }

        return sortItems(normalizedItems)
    }
}