package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.InventoryListDao
import com.example.depotakipai.data.model.InventoryListEntity
import com.example.depotakipai.domain.model.InventoryList

class UnapproveInventoryListUseCase(
    private val dao: InventoryListDao
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ) {
        val draftList = InventoryListEntity(
            id = inventoryList.id,
            type = inventoryList.type.name,
            createdAt = inventoryList.createdAt,
            productCount = inventoryList.productCount,
            totalQuantity = inventoryList.totalQuantity,
            sourcePhotoUri = inventoryList.sourcePhotoUri,
            isApproved = false,
            destination = inventoryList.destination
        )

        dao.updateList(draftList)
    }
}