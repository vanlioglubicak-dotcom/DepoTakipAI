package com.example.depotakipai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warehouse_locations")
data class WarehouseLocationEntity(
    @PrimaryKey
    val id: Long = 0L,
    val rackId: Long,
    val shelfNumber: Int,
    val locationNumber: Int,
    val position: String,
    val locationCode: String,
    val isActive: Boolean,
    val createdAt: Long
)