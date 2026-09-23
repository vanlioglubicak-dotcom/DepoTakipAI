package com.example.depotakipai.data.repository

import com.example.depotakipai.data.local.WarehouseDao
import com.example.depotakipai.data.model.WarehouseLocationEntity
import com.example.depotakipai.data.model.WarehouseRackEntity
import com.example.depotakipai.data.model.WarehouseRowEntity
import com.example.depotakipai.domain.model.ShelfPosition
import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow
import com.example.depotakipai.domain.repository.WarehouseRepository

class WarehouseRepositoryImpl(
    private val warehouseDao: WarehouseDao
) : WarehouseRepository {

    // ---------------------------------------------------------
    // SIRA
    // ---------------------------------------------------------

    override suspend fun getAllRows(): List<WarehouseRow> {
        return warehouseDao.getAllRows().map { it.toDomain() }
    }

    override suspend fun getActiveRows(): List<WarehouseRow> {
        return warehouseDao.getActiveRows().map { it.toDomain() }
    }

    override suspend fun getRowById(
        rowId: Long
    ): WarehouseRow? {
        return warehouseDao.getRowById(rowId)?.toDomain()
    }

    override suspend fun insertRow(
        row: WarehouseRow
    ): Long {
        return warehouseDao.insertRow(
            row.toEntity()
        )
    }

    override suspend fun insertRows(
        rows: List<WarehouseRow>
    ) {
        warehouseDao.insertRows(
            rows.map { it.toEntity() }
        )
    }

    override suspend fun updateRow(
        row: WarehouseRow
    ) {
        warehouseDao.updateRow(
            row.toEntity()
        )
    }

    override suspend fun deleteRow(
        row: WarehouseRow
    ) {
        warehouseDao.deleteRow(
            row.toEntity()
        )
    }

    // ---------------------------------------------------------
    // RAF
    // ---------------------------------------------------------

    override suspend fun getAllRacks(): List<WarehouseRack> {
        return warehouseDao.getAllRacks().map { it.toDomain() }
    }

    override suspend fun getRacksByRow(
        rowId: Long
    ): List<WarehouseRack> {
        return warehouseDao.getRacksByRow(rowId).map { it.toDomain() }
    }

    override suspend fun getRackById(
        rackId: Long
    ): WarehouseRack? {
        return warehouseDao.getRackById(rackId)?.toDomain()
    }

    override suspend fun getRackByCode(
        rowId: Long,
        rackCode: String
    ): WarehouseRack? {
        return warehouseDao.getRackByCode(
            rowId = rowId,
            rackCode = rackCode.trim().uppercase()
        )?.toDomain()
    }

    override suspend fun insertRack(
        rack: WarehouseRack
    ): Long {
        return warehouseDao.insertRack(
            rack.toEntity()
        )
    }

    override suspend fun insertRacks(
        racks: List<WarehouseRack>
    ) {
        warehouseDao.insertRacks(
            racks.map { it.toEntity() }
        )
    }

    override suspend fun updateRack(
        rack: WarehouseRack
    ) {
        warehouseDao.updateRack(
            rack.toEntity()
        )
    }

    override suspend fun deleteRack(
        rack: WarehouseRack
    ) {
        warehouseDao.deleteRack(
            rack.toEntity()
        )
    }

    // ---------------------------------------------------------
    // KONUM
    // ---------------------------------------------------------

    override suspend fun getAllLocations(): List<WarehouseLocation> {
        return warehouseDao.getAllLocations().map { it.toDomain() }
    }

    override suspend fun getLocationsByRack(
        rackId: Long
    ): List<WarehouseLocation> {
        return warehouseDao.getLocationsByRack(rackId).map {
            it.toDomain()
        }
    }

    override suspend fun getLocationById(
        locationId: Long
    ): WarehouseLocation? {
        return warehouseDao.getLocationById(locationId)?.toDomain()
    }

    override suspend fun getLocationByCode(
        locationCode: String
    ): WarehouseLocation? {
        return warehouseDao.getLocationByCode(
            locationCode.trim().uppercase()
        )?.toDomain()
    }

    override suspend fun insertLocation(
        location: WarehouseLocation
    ): Long {
        return warehouseDao.insertLocation(
            location.toEntity()
        )
    }

    override suspend fun insertLocations(
        locations: List<WarehouseLocation>
    ) {
        warehouseDao.insertLocations(
            locations.map { it.toEntity() }
        )
    }

    override suspend fun updateLocation(
        location: WarehouseLocation
    ) {
        warehouseDao.updateLocation(
            location.toEntity()
        )
    }

    override suspend fun deleteLocation(
        location: WarehouseLocation
    ) {
        warehouseDao.deleteLocation(
            location.toEntity()
        )
    }

    // ---------------------------------------------------------
    // MAPPERS
    // ---------------------------------------------------------

    private fun WarehouseRowEntity.toDomain(): WarehouseRow {
        return WarehouseRow(
            id = id,
            rowNumber = rowNumber,
            name = name,
            displayOrder = displayOrder,
            isActive = isActive,
            createdAt = createdAt
        )
    }

    private fun WarehouseRow.toEntity(): WarehouseRowEntity {
        return WarehouseRowEntity(
            id = id,
            rowNumber = rowNumber,
            name = name,
            displayOrder = displayOrder,
            isActive = isActive,
            createdAt = createdAt
        )
    }

    private fun WarehouseRackEntity.toDomain(): WarehouseRack {
        return WarehouseRack(
            id = id,
            rowId = rowId,
            rackCode = rackCode,
            name = name,
            displayOrder = displayOrder,
            shelfCount = shelfCount,
            isActive = isActive,
            createdAt = createdAt
        )
    }

    private fun WarehouseRack.toEntity(): WarehouseRackEntity {
        return WarehouseRackEntity(
            id = id,
            rowId = rowId,
            rackCode = rackCode,
            name = name,
            displayOrder = displayOrder,
            shelfCount = shelfCount,
            isActive = isActive,
            createdAt = createdAt
        )
    }

    private fun WarehouseLocationEntity.toDomain(): WarehouseLocation {
        return WarehouseLocation(
            id = id,
            rackId = rackId,
            shelfNumber = shelfNumber,
            locationNumber = locationNumber,
            position = position.toShelfPosition(),
            locationCode = locationCode,
            isActive = isActive,
            createdAt = createdAt
        )
    }

    private fun WarehouseLocation.toEntity(): WarehouseLocationEntity {
        return WarehouseLocationEntity(
            id = id,
            rackId = rackId,
            shelfNumber = shelfNumber,
            locationNumber = locationNumber,
            position = position.name,
            locationCode = locationCode,
            isActive = isActive,
            createdAt = createdAt
        )
    }

    private fun String.toShelfPosition(): ShelfPosition {
        return when (trim().uppercase()) {
            "BACK",
            "ARKA" -> ShelfPosition.BACK

            else -> ShelfPosition.FRONT
        }
    }
}