package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.InventoryListDao
import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.model.InventoryListItem
import com.example.depotakipai.domain.model.InventoryListType

class GetInventoryListWithItemsUseCase(
    private val dao: InventoryListDao
) {

    suspend operator fun invoke(
        listId: Long
    ): InventoryList? {

        val listEntity = dao.getListById(listId)
            ?: return null

        val itemEntities = dao.getItemsByListId(listId)

        val items = itemEntities.map { entity ->
            InventoryListItem(
                id = entity.id,
                listId = entity.listId,
                productCode = entity.productCode,
                systemBarcode = entity.systemBarcode,
                color = entity.color,
                size = entity.size,
                quantity = entity.quantity
            )
        }

        return InventoryList(
            id = listEntity.id,
            type = InventoryListType.valueOf(listEntity.type),
            createdAt = listEntity.createdAt,
            productCount = listEntity.productCount,
            totalQuantity = listEntity.totalQuantity,
            items = items,
            sourcePhotoUri = listEntity.sourcePhotoUri,
            isApproved = listEntity.isApproved,
            destination = listEntity.destination
        )
    }
}