package com.example.depotakipai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import com.example.depotakipai.data.model.StockMovementEntity

@Dao
interface StockMovementDao {

    @Insert
    suspend fun insertMovement(
        movement: StockMovementEntity
    ): Long

    @Delete
    suspend fun deleteMovement(
        movement: StockMovementEntity
    )

    @Query(
        "SELECT * FROM stock_movements " +
                "ORDER BY createdAt DESC"
    )
    suspend fun getAllMovements(): List<StockMovementEntity>

    @Query(
        "SELECT * FROM stock_movements " +
                "WHERE listId = :listId " +
                "ORDER BY createdAt ASC"
    )
    suspend fun getMovementsByListId(
        listId: Long
    ): List<StockMovementEntity>

    @Query(
        "SELECT * FROM stock_movements " +
                "WHERE productCode = :productCode " +
                "ORDER BY createdAt DESC"
    )
    suspend fun getMovementsByProductCode(
        productCode: String
    ): List<StockMovementEntity>

    @Query(
        "SELECT COUNT(*) FROM stock_movements " +
                "WHERE listId = :listId"
    )
    suspend fun countMovementsByListId(
        listId: Long
    ): Int

    @Query(
        "SELECT COUNT(*) FROM stock_movements " +
                "WHERE listId = :listId " +
                "AND productCode = :productCode"
    )
    suspend fun countProductMovements(
        listId: Long,
        productCode: String
    ): Int

    @Query(
        "DELETE FROM stock_movements " +
                "WHERE listId = :listId"
    )
    suspend fun deleteMovementsByListId(
        listId: Long
    )
}