package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListType

class GetInventoryListTypeLabelUseCase {

    operator fun invoke(
        type: InventoryListType
    ): String {
        return when (type) {
            InventoryListType.ANA_LISTE -> "Ana Liste"
            InventoryListType.YENI_GELEN -> "Yeni Gelen"
            InventoryListType.GIDEN -> "Giden"
            InventoryListType.GIDEN_IADE -> "Giden İade"
            InventoryListType.GELEN_IADE -> "Gelen İade"
        }
    }
}