package com.example.depotakipai.domain.usecase.warehouse

import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow
import com.example.depotakipai.domain.repository.WarehouseRepository

data class WarehouseStructure(
    val rows: List<WarehouseRow>,
    val racks: Map<Long, List<WarehouseRack>>
)

class GetWarehouseStructureUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(): WarehouseStructure {
        val rows = warehouseRepository.getActiveRows()

        val racks = rows.associate { row ->
            row.id to warehouseRepository.getRacksByRow(row.id)
        }

        return WarehouseStructure(
            rows = rows,
            racks = racks
        )
    }
}