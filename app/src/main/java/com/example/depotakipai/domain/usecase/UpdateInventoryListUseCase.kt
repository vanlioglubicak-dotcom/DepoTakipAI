package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.repository.InventoryListRepository

class UpdateInventoryListUseCase(
    private val repository: InventoryListRepository
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ) {
        repository.updateList(inventoryList)
    }
}