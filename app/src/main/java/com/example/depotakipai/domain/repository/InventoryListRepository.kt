package com.example.depotakipai.domain.repository

import com.example.depotakipai.domain.model.InventoryList

interface InventoryListRepository {

    suspend fun getAllLists(): List<InventoryList>

    suspend fun getListsByType(
        type: String
    ): List<InventoryList>

    suspend fun getListById(
        listId: Long
    ): InventoryList?

    suspend fun insertList(
        inventoryList: InventoryList
    ): Long

    suspend fun updateList(
        inventoryList: InventoryList
    )

    suspend fun deleteList(
        inventoryList: InventoryList
    )
}