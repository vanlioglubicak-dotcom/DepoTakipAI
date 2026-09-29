package com.example.depotakipai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.depotakipai.data.local.entity.DepotListRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepotListRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DepotListRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<DepotListRecordEntity>)

    @Delete
    suspend fun delete(record: DepotListRecordEntity)

    @Query("DELETE FROM depot_list_records")
    suspend fun deleteAll()

    @Query(
        """
        SELECT *
        FROM depot_list_records
        ORDER BY createdAt DESC
        """
    )
    fun getAll(): Flow<List<DepotListRecordEntity>>

    @Query(
        """
        SELECT *
        FROM depot_list_records
        WHERE category = :category
        ORDER BY createdAt DESC
        """
    )
    fun getByCategory(category: String): Flow<List<DepotListRecordEntity>>

    @Query(
        """
        SELECT *
        FROM depot_list_records
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getById(id: String): DepotListRecordEntity?

    @Query(
        """
        DELETE FROM depot_list_records
        WHERE id = :id
        """
    )
    suspend fun deleteById(id: String)
}