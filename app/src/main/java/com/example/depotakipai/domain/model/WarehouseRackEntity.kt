package com.example.depotakipai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warehouse_racks")
data class WarehouseRackEntity(
    @PrimaryKey
    val id: Long = 0L,
    val rowId: Long,
    val rackCode: String,
    val name: String,
    val displayOrder: Int,
    val shelfCount: Int,
    val isActive: Boolean,
    val createdAt: Long
)