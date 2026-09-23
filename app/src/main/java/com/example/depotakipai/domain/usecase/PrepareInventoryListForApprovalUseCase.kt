package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList

class PrepareInventoryListForApprovalUseCase(
    private val prepareCompleteList: PrepareCompleteInventoryListUseCase,
    private val validateInventoryList: ValidateInventoryListUseCase
) {

    operator fun invoke(
        inventoryList: InventoryList
    ): InventoryList? {

        // Onaylanmış bir liste tekrar onay hazırlığına
        // doğrudan alınmaz.
        if (inventoryList.isApproved) {
            return null
        }

        // Liste ve ürünleri son kez hazırla.
        val preparedList = prepareCompleteList(
            inventoryList
        ) ?: return null

        // Onay öncesi son doğrulama.
        if (!validateInventoryList(preparedList)) {
            return null
        }

        // Onay aşamasına hazır, ancak henüz onaylanmamış liste.
        return preparedList.copy(
            isApproved = false
        )
    }
}