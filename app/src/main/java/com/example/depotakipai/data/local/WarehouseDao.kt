package com.example.depotakipai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.depotakipai.data.model.WarehouseLocationEntity
import com.example.depotakipai.data.model.WarehousePhotoEntity
import com.example.depotakipai.data.model.WarehouseRackEntity
import com.example.depotakipai.data.model.WarehouseRowEntity

@Dao
interface WarehouseDao {

    // ---------------------------------------------------------
    // SIRA
    // ---------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRow(row: WarehouseRowEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRows(rows: List<WarehouseRowEntity>)

    @Update
    suspend fun updateRow(row: WarehouseRowEntity)

    @Delete
    suspend fun deleteRow(row: WarehouseRowEntity)

    @Query(
        """
        SELECT * FROM warehouse_rows
        ORDER BY displayOrder ASC
        """
    )
    suspend fun getAllRows(): List<WarehouseRowEntity>

    @Query(
        """
        SELECT * FROM warehouse_rows
        WHERE id = :rowId
        LIMIT 1
        """
    )
    suspend fun getRowById(rowId: Long): WarehouseRowEntity?

    @Query(
        """
        SELECT * FROM warehouse_rows
        WHERE isActive = 1
        ORDER BY displayOrder ASC
        """
    )
    suspend fun getActiveRows(): List<WarehouseRowEntity>

    // ---------------------------------------------------------
    // RAF
    // ---------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRack(rack: WarehouseRackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRacks(racks: List<WarehouseRackEntity>)

    @Update
    suspend fun updateRack(rack: WarehouseRackEntity)

    @Delete
    suspend fun deleteRack(rack: WarehouseRackEntity)

    @Query(
        """
        SELECT * FROM warehouse_racks
        ORDER BY rowId ASC, displayOrder ASC
        """
    )
    suspend fun getAllRacks(): List<WarehouseRackEntity>

    @Query(
        """
        SELECT * FROM warehouse_racks
        WHERE rowId = :rowId
        ORDER BY displayOrder ASC
        """
    )
    suspend fun getRacksByRow(rowId: Long): List<WarehouseRackEntity>

    @Query(
        """
        SELECT * FROM warehouse_racks
        WHERE id = :rackId
        LIMIT 1
        """
    )
    suspend fun getRackById(rackId: Long): WarehouseRackEntity?

    @Query(
        """
        SELECT * FROM warehouse_racks
        WHERE rackCode = :rackCode
        AND rowId = :rowId
        LIMIT 1
        """
    )
    suspend fun getRackByCode(
        rowId: Long,
        rackCode: String
    ): WarehouseRackEntity?

    // ---------------------------------------------------------
    // KONUM
    // ---------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: WarehouseLocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(
        locations: List<WarehouseLocationEntity>
    )

    @Update
    suspend fun updateLocation(location: WarehouseLocationEntity)

    @Delete
    suspend fun deleteLocation(location: WarehouseLocationEntity)

    @Query(
        """
        SELECT * FROM warehouse_locations
        ORDER BY rackId ASC,
                 shelfNumber ASC,
                 locationNumber ASC
        """
    )
    suspend fun getAllLocations(): List<WarehouseLocationEntity>

    @Query(
        """
        SELECT * FROM warehouse_locations
        WHERE rackId = :rackId
        ORDER BY shelfNumber ASC,
                 locationNumber ASC
        """
    )
    suspend fun getLocationsByRack(
        rackId: Long
    ): List<WarehouseLocationEntity>

    @Query(
        """
        SELECT * FROM warehouse_locations
        WHERE id = :locationId
        LIMIT 1
        """
    )
    suspend fun getLocationById(
        locationId: Long
    ): WarehouseLocationEntity?

    @Query(
        """
        SELECT * FROM warehouse_locations
        WHERE locationCode = :locationCode
        LIMIT 1
        """
    )
    suspend fun getLocationByCode(
        locationCode: String
    ): WarehouseLocationEntity?

    // ---------------------------------------------------------
    // FOTOĞRAF
    // ---------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(
        photo: WarehousePhotoEntity
    ): Long

    @Update
    suspend fun updatePhoto(
        photo: WarehousePhotoEntity
    )

    @Delete
    suspend fun deletePhoto(
        photo: WarehousePhotoEntity
    )

    @Query(
        """
        SELECT * FROM warehouse_photos
        ORDER BY createdAt DESC
        """
    )
    suspend fun getAllPhotos(): List<WarehousePhotoEntity>

    @Query(
        """
        SELECT * FROM warehouse_photos
        WHERE rackId = :rackId
        ORDER BY createdAt DESC
        """
    )
    suspend fun getPhotosByRack(
        rackId: Long
    ): List<WarehousePhotoEntity>

    @Query(
        """
        SELECT * FROM warehouse_photos
        WHERE locationId = :locationId
        ORDER BY createdAt DESC
        """
    )
    suspend fun getPhotosByLocation(
        locationId: Long
    ): List<WarehousePhotoEntity>

    @Query(
        """
        DELETE FROM warehouse_photos
        WHERE rackId = :rackId
        """
    )
    suspend fun deletePhotosByRack(
        rackId: Long
    )

    @Query(
        """
        DELETE FROM warehouse_photos
        WHERE locationId = :locationId
        """
    )
    suspend fun deletePhotosByLocation(
        locationId: Long
    )
}