package com.example.depotakipai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warehouse_photos")
data class WarehousePhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val rackId: Long? = null,
    val locationId: Long? = null,
    val photoUri: String,
    val title: String = "",
    val createdAt: Long
)