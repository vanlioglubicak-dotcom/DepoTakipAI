package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Stock
import com.example.depotakipai.domain.repository.StockRepository

class GetStocksByRowUseCase(
    private val repository: StockRepository
) {

    suspend operator fun invoke(
        rowNumber: Int
    ): List<Stock> {

        if (rowNumber <= 0) {
            return emptyList()
        }

        return repository.getStocksByRow(
            rowNumber
        )
    }
}