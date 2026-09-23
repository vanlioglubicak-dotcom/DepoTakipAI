package com.example.depotakipai.domain.model

data class WarehouseRack(
    val id: Long = 0L,
    val rowId: Long,
    val rackCode: String,
    val name: String = rackCode,
    val displayOrder: Int = 0,
    val shelfCount: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)