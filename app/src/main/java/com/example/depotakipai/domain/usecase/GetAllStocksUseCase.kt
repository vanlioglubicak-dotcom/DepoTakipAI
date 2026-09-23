package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Stock
import com.example.depotakipai.domain.repository.StockRepository

class GetAllStocksUseCase(
    private val repository: StockRepository
) {

    suspend operator fun invoke(): List<Stock> {
        return repository.getAllStocks()
    }
}