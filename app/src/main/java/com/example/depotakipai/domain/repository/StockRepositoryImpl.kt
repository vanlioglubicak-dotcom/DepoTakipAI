package com.example.depotakipai.data.repository

import com.example.depotakipai.data.local.StockDao
import com.example.depotakipai.data.model.StockEntity
import com.example.depotakipai.domain.model.ShelfPosition
import com.example.depotakipai.domain.model.Stock
import com.example.depotakipai.domain.repository.StockRepository

class StockRepositoryImpl(
    private val stockDao: StockDao
) : StockRepository {

    override suspend fun getAllStocks(): List<Stock> {
        return stockDao
            .getAllStocks()
            .map { it.toDomain() }
    }

    override suspend fun getStockByProductCode(
        productCode: String
    ): Stock? {
        return stockDao
            .getStockByProductCode(productCode)
            ?.toDomain()
    }

    override suspend fun getStockBySystemBarcode(
        systemBarcode: String
    ): Stock? {
        return stockDao
            .getStockBySystemBarcode(systemBarcode)
            ?.toDomain()
    }

    override suspend fun getStocksByColorAndSize(
        color: String,
        size: String
    ): List<Stock> {
        return stockDao
            .getStocksByColorAndSize(color, size)
            .map { it.toDomain() }
    }

    override suspend fun getStocksByRow(
        rowNumber: Int
    ): List<Stock> {
        return stockDao
            .getStocksByRow(rowNumber)
            .map { it.toDomain() }
    }

    override suspend fun insertStock(
        stock: Stock
    ) {
        stockDao.insertStock(
            stock.toEntity()
        )
    }

    override suspend fun updateStock(
        stock: Stock
    ) {
        stockDao.updateStock(
            stock.toEntity()
        )
    }

    override suspend fun deleteStock(
        stock: Stock
    ) {
        stockDao.deleteStock(
            stock.toEntity()
        )
    }

    override suspend fun increaseQuantity(
        productCode: String,
        quantity: Int,
        updatedAt: Long
    ) {
        stockDao.increaseQuantity(
            productCode = productCode,
            quantity = quantity,
            updatedAt = updatedAt
        )
    }

    override suspend fun decreaseQuantity(
        productCode: String,
        quantity: Int,
        updatedAt: Long
    ) {
        stockDao.decreaseQuantity(
            productCode = productCode,
            quantity = quantity,
            updatedAt = updatedAt
        )
    }

    override suspend fun deleteByProductCode(
        productCode: String
    ) {
        stockDao.deleteByProductCode(
            productCode
        )
    }

    private fun StockEntity.toDomain(): Stock {
        return Stock(
            productCode = productCode,
            systemBarcode = systemBarcode,
            color = color,
            size = size,
            quantity = quantity,
            rowNumber = rowNumber,
            shelfNumber = shelfNumber,
            position = position?.toShelfPosition(),
            updatedAt = updatedAt
        )
    }

    private fun Stock.toEntity(): StockEntity {
        return StockEntity(
            productCode = productCode,
            systemBarcode = systemBarcode,
            color = color,
            size = size,
            quantity = quantity,
            rowNumber = rowNumber,
            shelfNumber = shelfNumber,
            position = position?.name,
            updatedAt = updatedAt
        )
    }

    private fun String.toShelfPosition(): ShelfPosition? {
        return when (uppercase()) {
            "FRONT" -> ShelfPosition.FRONT
            "BACK" -> ShelfPosition.BACK
            else -> null
        }
    }
}