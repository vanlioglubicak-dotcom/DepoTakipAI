package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.repository.InventoryListRepository

class GetInventoryListsByTypeUseCase(
    private val repository: InventoryListRepository
) {

    suspend operator fun invoke(
        type: String
    ): List<InventoryList> {
        return repository.getListsByType(type)
    }
}