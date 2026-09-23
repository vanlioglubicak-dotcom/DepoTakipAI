package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.StockMovement
import com.example.depotakipai.domain.repository.StockMovementRepository

class SaveStockMovementUseCase(
    private val repository: StockMovementRepository
) {

    suspend operator fun invoke(
        movement: StockMovement
    ): Long {

        if (movement.listId <= 0L) {
            return 0L
        }

        val productCode = movement.productCode
            .trim()
            .uppercase()

        if (productCode.isEmpty()) {
            return 0L
        }

        if (movement.quantity <= 0) {
            return 0L
        }

        return repository.insertMovement(
            movement.copy(
                productCode = productCode,
                color = movement.color.trim().uppercase(),
                size = movement.size.trim().uppercase(),
                destination = movement.destination
                    ?.trim()
                    ?.ifEmpty { null }
            )
        )
    }
}