package com.example.depotakipai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.depotakipai.data.local.entity.CatalogItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CatalogItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CatalogItemEntity>)

    @Delete
    suspend fun delete(item: CatalogItemEntity)

    @Query("DELETE FROM catalog_items")
    suspend fun deleteAll()

    @Query("DELETE FROM catalog_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM catalog_items ORDER BY createdAt DESC")
    fun getAll(): Flow<List<CatalogItemEntity>>

    @Query(
        """
        SELECT * FROM catalog_items
        WHERE productNumber = :productNumber
        ORDER BY createdAt DESC
        """
    )
    fun getByProductNumber(
        productNumber: String
    ): Flow<List<CatalogItemEntity>>

    @Query(
        """
        SELECT * FROM catalog_items
        WHERE company = :company
        ORDER BY createdAt DESC
        """
    )
    fun getByCompany(
        company: String
    ): Flow<List<CatalogItemEntity>>

    @Query(
        """
        SELECT * FROM catalog_items
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getById(id: String): CatalogItemEntity?
}