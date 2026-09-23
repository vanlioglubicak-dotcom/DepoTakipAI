package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class PrepareInventoryListForEditUseCase(
    private val prepareNormalizedList: PrepareNormalizedInventoryListUseCase
) {

    operator fun invoke(
        inventoryList: InventoryList
    ): InventoryList? {

        val preparedList = prepareNormalizedList(
            inventoryList.copy(
                isApproved = false
            )
        ) ?: return null

        return preparedList.copy(
            isApproved = false
        )
    }
}