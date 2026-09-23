package com.example.depotakipai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.depotakipai.data.model.StockEntity

@Dao
interface StockDao {

    @Insert
    suspend fun insertStock(stock: StockEntity)

    @Update
    suspend fun updateStock(stock: StockEntity)

    @Delete
    suspend fun deleteStock(stock: StockEntity)

    @Query("SELECT * FROM stocks ORDER BY productCode ASC")
    suspend fun getAllStocks(): List<StockEntity>

    @Query(
        "SELECT * FROM stocks " +
                "WHERE productCode = :productCode LIMIT 1"
    )
    suspend fun getStockByProductCode(
        productCode: String
    ): StockEntity?

    @Query(
        "SELECT * FROM stocks " +
                "WHERE systemBarcode = :systemBarcode LIMIT 1"
    )
    suspend fun getStockBySystemBarcode(
        systemBarcode: String
    ): StockEntity?

    @Query(
        "SELECT * FROM stocks " +
                "WHERE color = :color " +
                "AND size = :size " +
                "ORDER BY productCode ASC"
    )
    suspend fun getStocksByColorAndSize(
        color: String,
        size: String
    ): List<StockEntity>

    @Query(
        "SELECT * FROM stocks " +
                "WHERE rowNumber = :rowNumber " +
                "ORDER BY shelfNumber ASC"
    )
    suspend fun getStocksByRow(
        rowNumber: Int
    ): List<StockEntity>

    @Query(
        "UPDATE stocks " +
                "SET quantity = quantity + :quantity, " +
                "updatedAt = :updatedAt " +
                "WHERE productCode = :productCode"
    )
    suspend fun increaseQuantity(
        productCode: String,
        quantity: Int,
        updatedAt: Long
    )

    @Query(
        "UPDATE stocks " +
                "SET quantity = quantity - :quantity, " +
                "updatedAt = :updatedAt " +
                "WHERE productCode = :productCode " +
                "AND quantity >= :quantity"
    )
    suspend fun decreaseQuantity(
        productCode: String,
        quantity: Int,
        updatedAt: Long
    )

    @Query(
        "DELETE FROM stocks " +
                "WHERE productCode = :productCode"
    )
    suspend fun deleteByProductCode(
        productCode: String
    )
}