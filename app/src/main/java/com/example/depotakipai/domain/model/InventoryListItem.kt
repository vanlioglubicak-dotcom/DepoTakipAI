package com.example.depotakipai.domain.model

data class InventoryListItem(
    val id: Long = 0L,
    val listId: Long = 0L,
    val productCode: String = "",
    val systemBarcode: String? = null,
    val color: String = "",
    val size: String = "",
    val quantity: Int = 0
)