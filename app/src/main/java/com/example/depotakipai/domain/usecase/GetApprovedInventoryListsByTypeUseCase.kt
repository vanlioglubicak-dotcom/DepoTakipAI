package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.InventoryListDao
import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.model.InventoryListItem
import com.example.depotakipai.domain.model.InventoryListType

class GetApprovedInventoryListsByTypeUseCase(
    private val dao: InventoryListDao
) {

    suspend operator fun invoke(
        type: InventoryListType
    ): List<InventoryList> {

        return dao.getApprovedListsByType(type.name).map { entity ->

            val items = dao.getItemsByListId(entity.id).map { item ->
                InventoryListItem(
                    id = item.id,
                    listId = item.listId,
                    productCode = item.productCode,
                    systemBarcode = item.systemBarcode,
                    color = item.color,
                    size = item.size,
                    quantity = item.quantity
                )
            }

            InventoryList(
                id = entity.id,
                type = InventoryListType.valueOf(entity.type),
                createdAt = entity.createdAt,
                productCount = entity.productCount,
                totalQuantity = entity.totalQuantity,
                items = items,
                sourcePhotoUri = entity.sourcePhotoUri,
                isApproved = entity.isApproved,
                destination = entity.destination
            )
        }
    }
}