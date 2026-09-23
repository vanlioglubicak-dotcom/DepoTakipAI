package com.example.depotakipai.domain.repository

import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow

interface WarehouseRepository {

    // Sıra
    suspend fun getAllRows(): List<WarehouseRow>

    suspend fun getActiveRows(): List<WarehouseRow>

    suspend fun getRowById(
        rowId: Long
    ): WarehouseRow?

    suspend fun insertRow(
        row: WarehouseRow
    ): Long

    suspend fun insertRows(
        rows: List<WarehouseRow>
    )

    suspend fun updateRow(
        row: WarehouseRow
    )

    suspend fun deleteRow(
        row: WarehouseRow
    )

    // Raf
    suspend fun getAllRacks(): List<WarehouseRack>

    suspend fun getRacksByRow(
        rowId: Long
    ): List<WarehouseRack>

    suspend fun getRackById(
        rackId: Long
    ): WarehouseRack?

    suspend fun getRackByCode(
        rowId: Long,
        rackCode: String
    ): WarehouseRack?

    suspend fun insertRack(
        rack: WarehouseRack
    ): Long

    suspend fun insertRacks(
        racks: List<WarehouseRack>
    )

    suspend fun updateRack(
        rack: WarehouseRack
    )

    suspend fun deleteRack(
        rack: WarehouseRack
    )

    // Konum
    suspend fun getAllLocations(): List<WarehouseLocation>

    suspend fun getLocationsByRack(
        rackId: Long
    ): List<WarehouseLocation>

    suspend fun getLocationById(
        locationId: Long
    ): WarehouseLocation?

    suspend fun getLocationByCode(
        locationCode: String
    ): WarehouseLocation?

    suspend fun insertLocation(
        location: WarehouseLocation
    ): Long

    suspend fun insertLocations(
        locations: List<WarehouseLocation>
    )

    suspend fun updateLocation(
        location: WarehouseLocation
    )

    suspend fun deleteLocation(
        location: WarehouseLocation
    )
}