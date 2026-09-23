package com.example.depotakipai.domain.repository

import com.example.depotakipai.domain.model.Stock

interface StockRepository {

    suspend fun getAllStocks(): List<Stock>

    suspend fun getStockByProductCode(
        productCode: String
    ): Stock?

    suspend fun getStockBySystemBarcode(
        systemBarcode: String
    ): Stock?

    suspend fun getStocksByColorAndSize(
        color: String,
        size: String
    ): List<Stock>

    suspend fun getStocksByRow(
        rowNumber: Int
    ): List<Stock>

    suspend fun insertStock(
        stock: Stock
    )

    suspend fun updateStock(
        stock: Stock
    )

    suspend fun deleteStock(
        stock: Stock
    )

    suspend fun increaseQuantity(
        productCode: String,
        quantity: Int,
        updatedAt: Long
    )

    suspend fun decreaseQuantity(
        productCode: String,
        quantity: Int,
        updatedAt: Long
    )

    suspend fun deleteByProductCode(
        productCode: String
    )
}