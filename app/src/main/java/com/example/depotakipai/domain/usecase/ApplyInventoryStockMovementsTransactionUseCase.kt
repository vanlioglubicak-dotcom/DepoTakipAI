package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.DatabaseTransactionRunner
import com.example.depotakipai.domain.model.StockMovement

class ApplyInventoryStockMovementsTransactionUseCase(
    private val transactionRunner: DatabaseTransactionRunner,
    private val ensureStockForMovement: EnsureStockForMovementUseCase,
    private val applyMovement: ApplyInventoryStockMovementUseCase,
    private val saveMovement: SaveStockMovementUseCase
) {

    suspend operator fun invoke(
        movements: List<PreparedInventoryStockMovement>
    ): Boolean {

        if (movements.isEmpty()) {
            return false
        }

        val listId = movements.first().listId

        if (listId <= 0L) {
            return false
        }

        if (movements.any { it.listId != listId }) {
            return false
        }

        return try {

            transactionRunner.run {

                for (movement in movements) {

                    if (movement.quantity <= 0) {
                        error("Geçersiz stok hareketi.")
                    }

                    val stockReady = ensureStockForMovement(
                        movement
                    )

                    if (!stockReady) {
                        error("Stok kaydı hazırlanamadı.")
                    }

                    val applied = applyMovement(
                        movement
                    )

                    if (!applied) {
                        error("Stok hareketi uygulanamadı.")
                    }

                    val stockMovement = StockMovement(
                        listId = movement.listId,
                        productCode = movement.productCode,
                        color = movement.color,
                        size = movement.size,
                        quantity = movement.quantity,
                        direction = movement.direction
                            .toStockMovementDirection(),
                        destination = movement.destination,
                        createdAt = System.currentTimeMillis()
                    )

                    val movementId = saveMovement(
                        stockMovement
                    )

                    if (movementId <= 0L) {
                        error("Stok hareketi kaydedilemedi.")
                    }
                }
            }

            true

        } catch (exception: Exception) {
            false
        }
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