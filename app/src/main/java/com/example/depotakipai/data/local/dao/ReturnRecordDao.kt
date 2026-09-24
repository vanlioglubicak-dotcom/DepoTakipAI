package com.example.depotakipai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.depotakipai.data.local.entity.ReturnRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReturnRecordDao {

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insert(
        returnRecord: ReturnRecordEntity
    ): Long

    @Update
    suspend fun update(
        returnRecord: ReturnRecordEntity
    )

    @Delete
    suspend fun delete(
        returnRecord: ReturnRecordEntity
    )

    @Query(
        "SELECT * FROM return_records " +
                "WHERE id = :id LIMIT 1"
    )
    suspend fun getById(
        id: Long
    ): ReturnRecordEntity?

    @Query(
        "SELECT * FROM return_records " +
                "ORDER BY createdAt DESC"
    )
    fun observeAll(): Flow<List<ReturnRecordEntity>>

    @Query(
        "SELECT * FROM return_records " +
                "WHERE type = 'INCOMING' " +
                "ORDER BY createdAt DESC"
    )
    fun observeIncomingReturns(): Flow<List<ReturnRecordEntity>>

    @Query(
        "SELECT * FROM return_records " +
                "WHERE type = 'OUTGOING' " +
                "ORDER BY createdAt DESC"
    )
    fun observeOutgoingReturns(): Flow<List<ReturnRecordEntity>>

    @Query(
        "SELECT * FROM return_records " +
                "WHERE status = :status " +
                "ORDER BY createdAt DESC"
    )
    fun observeByStatus(
        status: String
    ): Flow<List<ReturnRecordEntity>>
}