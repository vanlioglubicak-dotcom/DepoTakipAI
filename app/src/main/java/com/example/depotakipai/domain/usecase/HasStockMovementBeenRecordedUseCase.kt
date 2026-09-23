package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.repository.StockMovementRepository

class HasStockMovementBeenRecordedUseCase(
    private val repository: StockMovementRepository
) {

    suspend operator fun invoke(
        listId: Long
    ): Boolean {

        if (listId <= 0L) {
            return false
        }

        return repository.countMovementsByListId(
            listId
        ) > 0
    }
}