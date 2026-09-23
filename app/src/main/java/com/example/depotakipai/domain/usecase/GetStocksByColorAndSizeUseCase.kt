package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Stock
import com.example.depotakipai.domain.repository.StockRepository

class GetStocksByColorAndSizeUseCase(
    private val repository: StockRepository
) {

    suspend operator fun invoke(
        color: String,
        size: String
    ): List<Stock> {

        val normalizedColor = color
            .trim()
            .uppercase()

        val normalizedSize = size
            .trim()
            .uppercase()

        if (normalizedColor.isEmpty() || normalizedSize.isEmpty()) {
            return emptyList()
        }

        return repository.getStocksByColorAndSize(
            color = normalizedColor,
            size = normalizedSize
        )
    }
}