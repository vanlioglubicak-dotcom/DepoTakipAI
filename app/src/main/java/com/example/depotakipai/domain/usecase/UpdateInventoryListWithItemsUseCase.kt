package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.InventoryListDao
import com.example.depotakipai.data.model.InventoryListEntity
import com.example.depotakipai.data.model.InventoryListItemEntity
import com.example.depotakipai.domain.model.InventoryList

class UpdateInventoryListWithItemsUseCase(
    private val dao: InventoryListDao
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ) {
        val listEntity = InventoryListEntity(
            id = inventoryList.id,
            type = inventoryList.type.name,
            createdAt = inventoryList.createdAt,
            productCount = inventoryList.productCount,
            totalQuantity = inventoryList.totalQuantity,
            sourcePhotoUri = inventoryList.sourcePhotoUri,
            isApproved = inventoryList.isApproved,
            destination = inventoryList.destination
        )

        dao.updateList(listEntity)

        dao.deleteItemsByListId(inventoryList.id)

        if (inventoryList.items.isNotEmpty()) {
            val itemEntities = inventoryList.items.map { item ->
                InventoryListItemEntity(
                    id = 0L,
                    listId = inventoryList.id,
                    productCode = item.productCode,
                    systemBarcode = item.systemBarcode,
                    color = item.color,
                    size = item.size,
                    quantity = item.quantity
                )
            }

            dao.insertItems(itemEntities)
        }
    }
}