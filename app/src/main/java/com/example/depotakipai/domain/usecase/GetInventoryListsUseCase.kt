package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.repository.InventoryListRepository

class GetInventoryListsUseCase(
    private val repository: InventoryListRepository
) {

    suspend operator fun invoke(): List<InventoryList> {
        return repository.getAllLists()
    }
}