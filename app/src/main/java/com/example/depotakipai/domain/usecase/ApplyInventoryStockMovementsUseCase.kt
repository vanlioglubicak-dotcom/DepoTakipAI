package com.example.depotakipai.domain.usecase

class ApplyInventoryStockMovementsUseCase(
    private val ensureStockForMovement: EnsureStockForMovementUseCase,
    private val applyMovement: ApplyInventoryStockMovementUseCase
) {

    suspend operator fun invoke(
        movements: List<PreparedInventoryStockMovement>
    ): Boolean {

        if (movements.isEmpty()) {
            return false
        }

        for (movement in movements) {

            val stockReady = ensureStockForMovement(
                movement
            )

            if (!stockReady) {
                return false
            }

            val applied = applyMovement(
                movement
            )

            if (!applied) {
                return false
            }
        }

        return true
    }
}