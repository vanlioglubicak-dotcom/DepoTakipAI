package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class CreateCompleteInventoryListUseCase(
    private val prepareCompleteList: PrepareCompleteInventoryListUseCase,
    private val validateInventoryList: ValidateInventoryListUseCase,
    private val createInventoryList: CreateInventoryListUseCase
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ): Long? {

        // 1. Liste ve ürünleri tamamen hazırla.
        val preparedList = prepareCompleteList(
            inventoryList
        ) ?: return null

        // 2. Hazırlanan son listeyi doğrula.
        if (!validateInventoryList(preparedList)) {
            return null
        }

        // 3. Veritabanına kaydet.
        return createInventoryList(
            preparedList
        )
    }
}