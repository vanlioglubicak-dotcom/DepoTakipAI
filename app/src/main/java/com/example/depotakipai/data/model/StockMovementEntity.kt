package com.example.depotakipai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val listId: Long,

    val productCode: String,

    val color: String,

    val size: String,

    val quantity: Int,

    val direction: String,

    val destination: String? = null,

    val createdAt: Long
)