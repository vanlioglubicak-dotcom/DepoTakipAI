package com.example.depotakipai.domain.model

data class WarehouseLocation(
    val id: Long = 0L,
    val rackId: Long,
    val shelfNumber: Int,
    val locationNumber: Int,
    val position: ShelfPosition = ShelfPosition.FRONT,
    val locationCode: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)