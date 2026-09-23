package com.example.depotakipai.domain.usecase

class ApplyAndRecordStockMovementsUseCase(
    private val hasMovementBeenRecorded: HasStockMovementBeenRecordedUseCase,
    private val applyAndRecordMovement: ApplyAndRecordStockMovementUseCase
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

        val alreadyRecorded = hasMovementBeenRecorded(
            listId
        )

        if (alreadyRecorded) {
            return false
        }

        for (movement in movements) {

            if (movement.listId != listId) {
                return false
            }

            val applied = applyAndRecordMovement(
                movement
            )

            if (!applied) {
                return false
            }
        }

        return true
    }
}