package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Stock
import com.example.depotakipai.domain.repository.StockRepository

class GetStockBySystemBarcodeUseCase(
    private val repository: StockRepository
) {

    suspend operator fun invoke(
        systemBarcode: String
    ): Stock? {

        val normalizedBarcode = systemBarcode
            .trim()

        if (normalizedBarcode.isEmpty()) {
            return null
        }

        return repository.getStockBySystemBarcode(
            normalizedBarcode
        )
    }
}