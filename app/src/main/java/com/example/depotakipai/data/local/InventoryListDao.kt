package com.example.depotakipai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.depotakipai.data.model.InventoryListEntity
import com.example.depotakipai.data.model.InventoryListItemEntity

@Dao
interface InventoryListDao {

    // ---------------------------------------------------------
    // LİSTE KAYITLARI
    // ---------------------------------------------------------

    @Insert
    suspend fun insertList(
        list: InventoryListEntity
    ): Long

    @Update
    suspend fun updateList(
        list: InventoryListEntity
    )

    @Delete
    suspend fun deleteList(
        list: InventoryListEntity
    )

    @Query(
        "SELECT * FROM inventory_lists ORDER BY createdAt DESC"
    )
    suspend fun getAllLists(): List<InventoryListEntity>

    @Query(
        "SELECT * FROM inventory_lists WHERE id = :listId LIMIT 1"
    )
    suspend fun getListById(
        listId: Long
    ): InventoryListEntity?

    @Query(
        "SELECT * FROM inventory_lists WHERE type = :type ORDER BY createdAt DESC"
    )
    suspend fun getListsByType(
        type: String
    ): List<InventoryListEntity>

    // ---------------------------------------------------------
    // LİSTE ÜRÜNLERİ
    // ---------------------------------------------------------

    @Insert
    suspend fun insertItem(
        item: InventoryListItemEntity
    ): Long

    @Insert
    suspend fun insertItems(
        items: List<InventoryListItemEntity>
    )

    @Update
    suspend fun updateItem(
        item: InventoryListItemEntity
    )

    @Delete
    suspend fun deleteItem(
        item: InventoryListItemEntity
    )

    @Query(
        "SELECT * FROM inventory_list_items WHERE listId = :listId"
    )
    suspend fun getItemsByListId(
        listId: Long
    ): List<InventoryListItemEntity>

    @Query(
        "DELETE FROM inventory_list_items WHERE listId = :listId"
    )
    suspend fun deleteItemsByListId(
        listId: Long
    )
}