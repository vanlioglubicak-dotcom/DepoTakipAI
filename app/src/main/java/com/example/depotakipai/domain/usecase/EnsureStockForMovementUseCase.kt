package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Stock

class EnsureStockForMovementUseCase(
    private val repository: com.example.depotakipai.domain.repository.StockRepository
) {

    suspend operator fun invoke(
        movement: PreparedInventoryStockMovement
    ): Boolean {

        if (movement.quantity <= 0) {
            return false
        }

        val productCode = movement.productCode
            .trim()
            .uppercase()

        if (productCode.isEmpty()) {
            return false
        }

        val existingStock = repository.getStockByProductCode(
            productCode
        )

        if (existingStock != null) {
            return true
        }

        val stock = Stock(
            productCode = productCode,
            systemBarcode = null,
            color = movement.color.trim().uppercase(),
            size = movement.size.trim().uppercase(),
            quantity = 0,
            rowNumber = null,
            shelfNumber = null,
            position = null,
            updatedAt = System.currentTimeMillis()
        )

        repository.insertStock(stock)

        return true
    }
}