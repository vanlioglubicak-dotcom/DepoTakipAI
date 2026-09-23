package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.repository.StockRepository

class DecreaseStockQuantityUseCase(
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

        val stock = repository.getStockByProductCode(
            normalizedCode
        ) ?: return false

        if (stock.quantity < quantity) {
            return false
        }

        repository.decreaseQuantity(
            productCode = normalizedCode,
            quantity = quantity,
            updatedAt = updatedAt
        )

        return true
    }
}