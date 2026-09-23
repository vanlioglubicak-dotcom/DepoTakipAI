package com.example.depotakipai.data.repository

import com.example.depotakipai.data.local.StockMovementDao
import com.example.depotakipai.data.model.StockMovementEntity
import com.example.depotakipai.domain.model.StockMovement
import com.example.depotakipai.domain.model.StockMovementDirection
import com.example.depotakipai.domain.repository.StockMovementRepository

class StockMovementRepositoryImpl(
    private val stockMovementDao: StockMovementDao
) : StockMovementRepository {

    override suspend fun insertMovement(
        movement: StockMovement
    ): Long {
        return stockMovementDao.insertMovement(
            movement.toEntity()
        )
    }

    override suspend fun deleteMovement(
        movement: StockMovement
    ) {
        stockMovementDao.deleteMovement(
            movement.toEntity()
        )
    }

    override suspend fun getAllMovements(): List<StockMovement> {
        return stockMovementDao
            .getAllMovements()
            .map { it.toDomain() }
    }

    override suspend fun getMovementsByListId(
        listId: Long
    ): List<StockMovement> {
        return stockMovementDao
            .getMovementsByListId(listId)
            .map { it.toDomain() }
    }

    override suspend fun getMovementsByProductCode(
        productCode: String
    ): List<StockMovement> {
        return stockMovementDao
            .getMovementsByProductCode(
                productCode.trim().uppercase()
            )
            .map { it.toDomain() }
    }

    override suspend fun countMovementsByListId(
        listId: Long
    ): Int {
        return stockMovementDao
            .countMovementsByListId(listId)
    }

    override suspend fun countProductMovements(
        listId: Long,
        productCode: String
    ): Int {
        return stockMovementDao
            .countProductMovements(
                listId = listId,
                productCode = productCode.trim().uppercase()
            )
    }

    override suspend fun deleteMovementsByListId(
        listId: Long
    ) {
        stockMovementDao.deleteMovementsByListId(listId)
    }
}

private fun StockMovement.toEntity(): StockMovementEntity {
    return StockMovementEntity(
        id = id,
        listId = listId,
        productCode = productCode.trim().uppercase(),
        color = color.trim().uppercase(),
        size = size.trim().uppercase(),
        quantity = quantity,
        direction = direction.name,
        destination = destination?.trim()?.ifEmpty { null },
        createdAt = createdAt
    )
}

private fun StockMovementEntity.toDomain(): StockMovement {
    return StockMovement(
        id = id,
        listId = listId,
        productCode = productCode,
        color = color,
        size = size,
        quantity = quantity,
        direction = when (direction) {
            StockMovementDirection.INCREASE.name ->
                StockMovementDirection.INCREASE

            StockMovementDirection.DECREASE.name ->
                StockMovementDirection.DECREASE

            else ->
                StockMovementDirection.INCREASE
        },
        destination = destination,
        createdAt = createdAt
    )
}