package com.example.depotakipai.domain.usecase.warehouse

import com.example.depotakipai.domain.model.WarehouseRow
import com.example.depotakipai.domain.repository.WarehouseRepository

class InitializeWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(): List<WarehouseRow> {

        val existingRows =
            warehouseRepository.getAllRows()

        if (existingRows.isNotEmpty()) {
            return existingRows
                .sortedBy { it.displayOrder }
        }

        val defaultRows =
            WarehouseRow.createDefaultRows()

        warehouseRepository.insertRows(
            defaultRows
        )

        return defaultRows
    }
}