package com.example.depotakipai.domain.model

data class Warehouse(
    val id: Long = 1L,
    val name: String = "Ana Depo",
    val rowCount: Int = WarehouseRow.TOTAL_ROWS,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)