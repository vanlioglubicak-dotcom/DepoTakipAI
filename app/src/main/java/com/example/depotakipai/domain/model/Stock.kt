package com.example.depotakipai.domain.model

import com.example.depotakipai.domain.model.ShelfPosition

data class Stock(
    val productCode: String,
    val systemBarcode: String? = null,
    val color: String,
    val size: String,
    val quantity: Int = 0,
    val rowNumber: Int? = null,
    val shelfNumber: String? = null,
    val position: ShelfPosition? = null,
    val updatedAt: Long
)