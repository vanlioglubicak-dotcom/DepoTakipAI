package com.example.depotakipai.data.model

data class ProductEntity(
    val productCode: String,
    val systemBarcode: String?,
    val color: String,
    val size: String,
    val quantity: Int,
    val rowNumber: Int?,
    val shelfNumber: Int?,
    val position: String?,
    val createdAt: Long
)