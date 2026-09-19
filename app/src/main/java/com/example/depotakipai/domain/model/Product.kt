package com.example.depotakipai.domain.model

data class Product(
    val productCode: String,
    val systemBarcode: String?,
    val color: String,
    val size: String,
    val quantity: Int,
    val rowNumber: Int?,
    val shelfNumber: String?,
    val position: ShelfPosition?,
    val photoUri: String?,
    val createdAt: Long
)

enum class ShelfPosition {
    FRONT,
    BACK
}