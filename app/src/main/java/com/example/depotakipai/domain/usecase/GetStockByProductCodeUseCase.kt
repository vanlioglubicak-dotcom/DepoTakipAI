package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Stock
import com.example.depotakipai.domain.repository.StockRepository

class GetStockByProductCodeUseCase(
    private val repository: StockRepository
) {

    suspend operator fun invoke(
        productCode: String
    ): Stock? {
        val normalizedCode = productCode
            .trim()
            .uppercase()

        if (normalizedCode.isEmpty()) {
            return null
        }

        return repository.getStockByProductCode(
            normalizedCode
        )
    }
}