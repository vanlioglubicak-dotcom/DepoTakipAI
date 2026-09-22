package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListType

class ValidateInventoryListTypeUseCase {

    operator fun invoke(
        type: InventoryListType
    ): Boolean {

        return when (type) {
            InventoryListType.ANA_LISTE -> true
            InventoryListType.YENI_GELEN -> true
            InventoryListType.GIDEN -> true
            InventoryListType.GIDEN_IADE -> true
            InventoryListType.GELEN_IADE -> true
        }
    }
}