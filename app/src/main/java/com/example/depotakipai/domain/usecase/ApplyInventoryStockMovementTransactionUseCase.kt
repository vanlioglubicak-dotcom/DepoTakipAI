package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.DatabaseTransactionRunner
import com.example.depotakipai.domain.model.StockMovement

class ApplyInventoryStockMovementTransactionUseCase(
    private val transactionRunner: DatabaseTransactionRunner,
    private val ensureStockForMovement: EnsureStockForMovementUseCase,
    private val applyMovement: ApplyInventoryStockMovementUseCase,
    private val saveMovement: SaveStockMovementUseCase
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

        return try {

            transactionRunner.run {

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