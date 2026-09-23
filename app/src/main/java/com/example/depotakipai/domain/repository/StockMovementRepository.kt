package com.example.depotakipai.domain.repository

import com.example.depotakipai.domain.model.StockMovement

interface StockMovementRepository {

    suspend fun insertMovement(
        movement: StockMovement
    ): Long

    suspend fun deleteMovement(
        movement: StockMovement
    )

    suspend fun getAllMovements(): List<StockMovement>

    suspend fun getMovementsByListId(
        listId: Long
    ): List<StockMovement>

    suspend fun getMovementsByProductCode(
        productCode: String
    ): List<StockMovement>

    suspend fun countMovementsByListId(
        listId: Long
    ): Int

    suspend fun countProductMovements(
        listId: Long,
        productCode: String
    ): Int

    suspend fun deleteMovementsByListId(
        listId: Long
    )
}