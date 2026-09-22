package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class RejectInventoryListUseCase(
    private val unapproveInventoryList: UnapproveInventoryListUseCase
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ) {
        unapproveInventoryList(inventoryList)
    }
}