package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.repository.StockRepository

class IncreaseStockQuantityUseCase(
    private val repository: StockRepository
) {

    suspend operator fun invoke(
        productCode: String,
        quantity: Int,
        updatedAt: Long = System.currentTimeMillis()
    ): Boolean {

        val normalizedCode = productCode
            .trim()
            .uppercase()

        if (normalizedCode.isEmpty()) {
            return false
        }

        if (quantity <= 0) {
            return false
        }

        repository.increaseQuantity(
            productCode = normalizedCode,
            quantity = quantity,
            updatedAt = updatedAt
        )

        return true
    }
}