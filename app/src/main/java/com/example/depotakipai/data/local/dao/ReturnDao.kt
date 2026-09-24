package com.example.depotakipai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.depotakipai.data.local.entity.ReturnRecordEntity

@Dao
interface ReturnDao {

    @Insert
    suspend fun insertReturn(
        returnRecord: ReturnRecordEntity
    ): Long

    @Update
    suspend fun updateReturn(
        returnRecord: ReturnRecordEntity
    )

    @Delete
    suspend fun deleteReturn(
        returnRecord: ReturnRecordEntity
    )

    @Query(
        "SELECT * FROM return_records " +
                "ORDER BY createdAt DESC"
    )
    suspend fun getAllReturns(): List<ReturnRecordEntity>

    @Query(
        "SELECT * FROM return_records " +
                "WHERE id = :id LIMIT 1"
    )
    suspend fun getReturnById(
        id: Long
    ): ReturnRecordEntity?

    @Query(
        "SELECT * FROM return_records " +
                "WHERE type = :type " +
                "ORDER BY createdAt DESC"
    )
    suspend fun getReturnsByType(
        type: String
    ): List<ReturnRecordEntity>

    @Query(
        "DELETE FROM return_records"
    )
    suspend fun deleteAllReturns()
}