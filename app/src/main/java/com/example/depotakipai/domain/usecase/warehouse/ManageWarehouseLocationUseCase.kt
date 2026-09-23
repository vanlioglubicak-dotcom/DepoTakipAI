package com.example.depotakipai.domain.usecase.warehouse

import com.example.depotakipai.domain.model.ShelfPosition
import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.repository.WarehouseRepository

class ManageWarehouseLocationUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend fun addLocation(location: WarehouseLocation): Long {
        return warehouseRepository.insertLocation(location)
    }

    suspend fun updateLocation(location: WarehouseLocation) {
        warehouseRepository.updateLocation(location)
    }

    suspend fun deleteLocation(location: WarehouseLocation) {
        warehouseRepository.deleteLocation(location)
    }

    suspend fun getLocationsForRack(
        rackId: Long
    ): List<WarehouseLocation> {
        return warehouseRepository.getLocationsByRack(rackId)
    }

    suspend fun getLocationByCode(
        locationCode: String
    ): WarehouseLocation? {
        return warehouseRepository.getLocationByCode(locationCode)
    }

    fun createLocationCode(
        rowNumber: Int,
        rackCode: String,
        shelfNumber: Int,
        locationNumber: Int,
        position: ShelfPosition
    ): String {
        val positionCode = when (position) {
            ShelfPosition.FRONT -> "O"
            ShelfPosition.BACK -> "A"
        }

        return "S%02d-R%s-K%02d-L%02d-%s".format(
            rowNumber,
            rackCode.uppercase(),
            shelfNumber,
            locationNumber,
            positionCode
        )
    }
}