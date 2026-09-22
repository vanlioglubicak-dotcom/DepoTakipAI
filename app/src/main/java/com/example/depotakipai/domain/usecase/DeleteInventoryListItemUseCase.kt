package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.InventoryListDao
import com.example.depotakipai.domain.model.InventoryListItem

class DeleteInventoryListItemUseCase(
    private val dao: InventoryListDao
) {

    suspend operator fun invoke(
        item: InventoryListItem
    ) {
        dao.deleteItem(
            com.example.depotakipai.data.model.InventoryListItemEntity(
                id = item.id,
                listId = item.listId,
                productCode = item.productCode,
                systemBarcode = item.systemBarcode,
                color = item.color,
                size = item.size,
                quantity = item.quantity
            )
        )
    }
}