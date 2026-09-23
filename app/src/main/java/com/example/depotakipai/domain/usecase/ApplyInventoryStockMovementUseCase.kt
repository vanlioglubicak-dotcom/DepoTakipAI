package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListType
import com.example.depotakipai.domain.repository.StockRepository

class ApplyInventoryStockMovementUseCase(
    private val repository: StockRepository
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

        val stock = repository.getStockByProductCode(
            productCode
        ) ?: return false

        val updatedAt = System.currentTimeMillis()

        return when (movement.direction) {

            InventoryStockMovementDirection.INCREASE -> {
                repository.increaseQuantity(
                    productCode = productCode,
                    quantity = movement.quantity,
                    updatedAt = updatedAt
                )
                true
            }

            InventoryStockMovementDirection.DECREASE -> {

                if (stock.quantity < movement.quantity) {
                    false
                } else {
                    repository.decreaseQuantity(
                        productCode = productCode,
                        quantity = movement.quantity,
                        updatedAt = updatedAt
                    )
                    true
                }
            }

            InventoryStockMovementDirection.NONE -> {
                false
            }
        }
    }
}