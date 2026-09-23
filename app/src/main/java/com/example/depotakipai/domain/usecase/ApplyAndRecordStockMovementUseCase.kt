package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.StockMovement
import com.example.depotakipai.domain.repository.StockMovementRepository

class ApplyAndRecordStockMovementUseCase(
    private val hasMovementBeenRecorded: HasStockMovementBeenRecordedUseCase,
    private val applyMovement: ApplyInventoryStockMovementUseCase,
    private val saveMovement: SaveStockMovementUseCase,
    private val movementRepository: StockMovementRepository
) {

    suspend operator fun invoke(
        movement: PreparedInventoryStockMovement
    ): Boolean {

        if (movement.listId <= 0L) {
            return false
        }

        if (movement.quantity <= 0) {
            return false
        }

        val alreadyRecorded = hasMovementBeenRecorded(
            movement.listId
        )

        if (alreadyRecorded) {
            return false
        }

        val applied = applyMovement(
            movement
        )

        if (!applied) {
            return false
        }

        val stockMovement = StockMovement(
            listId = movement.listId,
            productCode = movement.productCode,
            color = movement.color,
            size = movement.size,
            quantity = movement.quantity,
            direction = movement.direction.toStockMovementDirection(),
            destination = movement.destination,
            createdAt = System.currentTimeMillis()
        )

        val movementId = saveMovement(
            stockMovement
        )

        if (movementId <= 0L) {
            return false
        }

        return true
    }

    private fun InventoryStockMovementDirection.toStockMovementDirection():
            com.example.depotakipai.domain.model.StockMovementDirection {

        return when (this) {
            InventoryStockMovementDirection.INCREASE ->
                com.example.depotakipai.domain.model.StockMovementDirection.INCREASE

            InventoryStockMovementDirection.DECREASE ->
                com.example.depotakipai.domain.model.StockMovementDirection.DECREASE

            InventoryStockMovementDirection.NONE ->
                com.example.depotakipai.domain.model.StockMovementDirection.INCREASE
        }
    }
}