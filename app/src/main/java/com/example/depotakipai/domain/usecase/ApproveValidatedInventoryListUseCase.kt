package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class ApproveValidatedInventoryListUseCase(
    private val validateInventoryList: ValidateInventoryListUseCase,
    private val approveInventoryList: ApproveInventoryListUseCase
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ): Boolean {

        // Liste önce tamamen doğrulanır.
        val isValid = validateInventoryList(inventoryList)

        if (!isValid) {
            return false
        }

        // Doğrulama başarılıysa liste onaylanır.
        approveInventoryList(inventoryList)

        return true
    }
}