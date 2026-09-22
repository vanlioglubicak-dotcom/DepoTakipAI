package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.InventoryListDao
import com.example.depotakipai.domain.model.InventoryListItem

class GetInventoryListItemsUseCase(
    private val dao: InventoryListDao
) {

    suspend operator fun invoke(
        listId: Long
    ): List<InventoryListItem> {

        return dao.getItemsByListId(listId).map { entity ->
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
    }
}