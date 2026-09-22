package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class CreatePreparedInventoryListUseCase(
    private val prepareNormalizedList: PrepareNormalizedInventoryListUseCase,
    private val validateInventoryList: ValidateInventoryListUseCase,
    private val createInventoryList: CreateInventoryListUseCase
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ): Long? {

        // 1. Ürünleri normalize et ve aynı kayıtları birleştir.
        val preparedList = prepareNormalizedList(
            inventoryList
        ) ?: return null

        // 2. Temizlenmiş listeyi tekrar doğrula.
        if (!validateInventoryList(preparedList)) {
            return null
        }

        // 3. Geçerli listeyi veritabanına kaydet.
        return createInventoryList(
            preparedList
        )
    }
}