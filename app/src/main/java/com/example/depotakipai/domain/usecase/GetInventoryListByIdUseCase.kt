package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.repository.InventoryListRepository

class GetInventoryListByIdUseCase(
    private val repository: InventoryListRepository
) {

    suspend operator fun invoke(
        listId: Long
    ): InventoryList? {
        return repository.getListById(listId)
    }
}