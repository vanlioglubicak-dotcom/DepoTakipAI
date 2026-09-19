package com.example.depotakipai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val productCode: String,
    val systemBarcode: String?,
    val color: String,
    val size: String,
    val quantity: Int,
    val rowNumber: Int?,
    val shelfNumber: String?,
    val position: String?,
    val photoUri: String?,
    val createdAt: Long
)