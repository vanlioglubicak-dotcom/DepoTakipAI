package com.example.depotakipai.domain.model

data class StockMovement(
    val id: Long = 0L,
    val listId: Long,
    val productCode: String,
    val color: String,
    val size: String,
    val quantity: Int,
    val direction: StockMovementDirection,
    val destination: String? = null,
    val createdAt: Long
)

enum class StockMovementDirection {
    INCREASE,
    DECREASE
}