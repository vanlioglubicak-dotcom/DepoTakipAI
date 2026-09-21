package com.example.depotakipai.data.repository

import com.example.depotakipai.data.local.InventoryListDao
import com.example.depotakipai.data.model.InventoryListEntity
import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.model.InventoryListType
import com.example.depotakipai.domain.repository.InventoryListRepository

class InventoryListRepositoryImpl(
    private val dao: InventoryListDao
) : InventoryListRepository {

    override suspend fun getAllLists(): List<InventoryList> {
        return dao.getAllLists().map { entity ->
            entity.toDomain()
        }
    }

    override suspend fun getListsByType(
        type: String
    ): List<InventoryList> {
        return dao.getListsByType(type).map { entity ->
            entity.toDomain()
        }
    }

    override suspend fun getListById(
        listId: Long
    ): InventoryList? {
        return dao.getListById(listId)?.toDomain()
    }

    override suspend fun insertList(
        inventoryList: InventoryList
    ): Long {
        return dao.insertList(
            inventoryList.toEntity()
        )
    }

    override suspend fun updateList(
        inventoryList: InventoryList
    ) {
        dao.updateList(
            inventoryList.toEntity()
        )
    }

    override suspend fun deleteList(
        inventoryList: InventoryList
    ) {
        dao.deleteList(
            inventoryList.toEntity()
        )
    }

    private fun InventoryListEntity.toDomain(): InventoryList {
        return InventoryList(
            id = id,
            type = InventoryListType.valueOf(type),
            createdAt = createdAt,
            productCount = productCount,
            totalQuantity = totalQuantity,
            items = emptyList(),
            sourcePhotoUri = sourcePhotoUri,
            isApproved = isApproved,
            destination = destination
        )
    }

    private fun InventoryList.toEntity(): InventoryListEntity {
        return InventoryListEntity(
            id = id,
            type = type.name,
            createdAt = createdAt,
            productCount = productCount,
            totalQuantity = totalQuantity,
            sourcePhotoUri = sourcePhotoUri,
            isApproved = isApproved,
            destination = destination
        )
    }
}