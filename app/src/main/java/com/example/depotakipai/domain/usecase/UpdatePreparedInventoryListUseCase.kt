package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class UpdatePreparedInventoryListUseCase(
    private val prepareNormalizedList: PrepareNormalizedInventoryListUseCase,
    private val validateInventoryList: ValidateInventoryListUseCase,
    private val updateInventoryList: UpdateInventoryListWithItemsUseCase
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ): Boolean {

        // 1. Ürünleri normalize et ve aynı kayıtları birleştir.
        val preparedList = prepareNormalizedList(
            inventoryList
        ) ?: return false

        // 2. Güncellenmiş listeyi tekrar doğrula.
        if (!validateInventoryList(preparedList)) {
            return false
        }

        // 3. Geçerli listeyi ve ürünlerini birlikte güncelle.
        updateInventoryList(
            preparedList
        )

        return true
    }
}