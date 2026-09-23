package com.example.depotakipai.domain.usecase.warehouse

import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.repository.WarehouseRepository

class GetWarehouseLocationsUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(
        rackId: Long
    ): List<WarehouseLocation> {

        return warehouseRepository.getLocationsByRack(
            rackId = rackId
        )
    }
}