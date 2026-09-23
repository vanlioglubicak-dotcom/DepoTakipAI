package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class ApprovePreparedInventoryListUseCase(
    private val prepareForApproval: PrepareInventoryListForApprovalUseCase,
    private val approveInventoryList: ApproveInventoryListUseCase
) {

    suspend operator fun invoke(
        inventoryList: InventoryList
    ): Boolean {

        // 1. Liste onay öncesi tamamen hazırlanır.
        val preparedList = prepareForApproval(
            inventoryList
        ) ?: return false

        // 2. Hazırlanmış liste onaylanır.
        approveInventoryList(
            preparedList
        )

        return true
    }
}