package com.example.depotakipai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey
    val productCode: String,

    val systemBarcode: String? = null,

    val color: String,

    val size: String,

    val quantity: Int = 0,

    val rowNumber: Int? = null,

    val shelfNumber: String? = null,

    val position: String? = null,

    val updatedAt: Long
)