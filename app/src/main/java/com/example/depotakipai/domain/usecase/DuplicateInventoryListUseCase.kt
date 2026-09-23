package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class DuplicateInventoryListUseCase(
    private val createInventoryList: CreateInventoryListUseCase,
    private val createTimestamp: CreateInventoryListTimestampUseCase
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ): Long? {

        if (inventoryList.items.isEmpty()) {
            return null
        }

        val duplicatedList = inventoryList.copy(
            id = 0L,
            createdAt = createTimestamp(),
            isApproved = false
        )

        return createInventoryList(
            duplicatedList
        )
    }
}