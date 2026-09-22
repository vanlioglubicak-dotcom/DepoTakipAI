package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.repository.InventoryListRepository

class InsertInventoryListUseCase(
    private val repository: InventoryListRepository
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ): Long {
        return repository.insertList(inventoryList)
    }
}