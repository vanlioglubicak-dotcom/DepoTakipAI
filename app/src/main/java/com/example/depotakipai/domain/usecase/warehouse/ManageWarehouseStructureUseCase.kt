package com.example.depotakipai.domain.usecase.warehouse

import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow
import com.example.depotakipai.domain.repository.WarehouseRepository

class ManageWarehouseStructureUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend fun addRow(row: WarehouseRow): Long {
        return warehouseRepository.insertRow(row)
    }

    suspend fun updateRow(row: WarehouseRow) {
        warehouseRepository.updateRow(row)
    }

    suspend fun deleteRow(row: WarehouseRow) {
        warehouseRepository.deleteRow(row)
    }

    suspend fun addRack(rack: WarehouseRack): Long {
        return warehouseRepository.insertRack(rack)
    }

    suspend fun updateRack(rack: WarehouseRack) {
        warehouseRepository.updateRack(rack)
    }

    suspend fun deleteRack(rack: WarehouseRack) {
        warehouseRepository.deleteRack(rack)
    }

    suspend fun getRacksForRow(rowId: Long): List<WarehouseRack> {
        return warehouseRepository.getRacksByRow(rowId)
    }
}