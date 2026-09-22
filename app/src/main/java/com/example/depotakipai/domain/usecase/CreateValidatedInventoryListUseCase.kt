package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class CreateValidatedInventoryListUseCase(
    private val prepareInventoryList: PrepareInventoryListUseCase,
    private val createInventoryList: CreateInventoryListUseCase
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ): Long? {

        // Liste oluşturulmadan önce hazırlanır ve doğrulanır.
        val preparedList = prepareInventoryList(inventoryList)
            ?: return null

        // Geçerli liste veritabanına taslak/onay durumu ile kaydedilir.
        return createInventoryList(preparedList)
    }
}