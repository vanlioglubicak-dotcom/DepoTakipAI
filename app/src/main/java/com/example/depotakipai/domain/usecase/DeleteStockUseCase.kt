package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.repository.StockRepository

class DeleteStockUseCase(
    private val repository: StockRepository
) {

    suspend operator fun invoke(
        productCode: String
    ): Boolean {

        val normalizedCode = productCode
            .trim()
            .uppercase()

        if (normalizedCode.isEmpty()) {
            return false
        }

        val existingStock = repository.getStockByProductCode(
            normalizedCode
        ) ?: return false

        repository.deleteStock(existingStock)

        return true
    }
}