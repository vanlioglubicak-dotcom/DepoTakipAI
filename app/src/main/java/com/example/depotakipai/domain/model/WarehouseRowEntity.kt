package com.example.depotakipai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warehouse_rows")
data class WarehouseRowEntity(
    @PrimaryKey
    val id: Long = 0L,
    val rowNumber: Int,
    val name: String,
    val displayOrder: Int,
    val isActive: Boolean,
    val createdAt: Long
)