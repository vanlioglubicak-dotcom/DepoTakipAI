package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.InventoryListDao
import com.example.depotakipai.domain.model.InventoryList

class DeleteInventoryListWithItemsUseCase(
    private val dao: InventoryListDao
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ) {
        dao.deleteItemsByListId(inventoryList.id)

        dao.deleteList(
            com.example.depotakipai.data.model.InventoryListEntity(
                id = inventoryList.id,
                type = inventoryList.type.name,
                createdAt = inventoryList.createdAt,
                productCount = inventoryList.productCount,
                totalQuantity = inventoryList.totalQuantity,
                sourcePhotoUri = inventoryList.sourcePhotoUri,
                isApproved = inventoryList.isApproved,
                destination = inventoryList.destination
            )
        )
    }
}