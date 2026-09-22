package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryList
import com.example.depotakipai.domain.model.InventoryListType

class ValidateInventoryListUseCase(
    private val validateItem: ValidateInventoryListItemUseCase,
    private val validateDestination: ValidateInventoryListDestinationUseCase
) {

    operator fun invoke(
        inventoryList: InventoryList
    ): Boolean {

        // Liste türü geçerli olmalıdır.
        val validType = when (inventoryList.type) {
            InventoryListType.ANA_LISTE,
            InventoryListType.YENI_GELEN,
            InventoryListType.GIDEN,
            InventoryListType.GIDEN_IADE,
            InventoryListType.GELEN_IADE -> true
        }

        if (!validType) {
            return false
        }

        // Liste boş bırakılamaz.
        if (inventoryList.items.isEmpty()) {
            return false
        }

        // Listedeki bütün ürün satırları geçerli olmalıdır.
        val allItemsValid = inventoryList.items.all { item ->
            validateItem(item)
        }

        if (!allItemsValid) {
            return false
        }

        // Giden İade hedefi kontrol edilir.
        if (!validateDestination(
                inventoryList.type,
                inventoryList.destination
            )
        ) {
            return false
        }

        // Ürün sayısı gerçek listedeki farklı ürün sayısıyla
        // aynı olmalıdır.
        val calculatedProductCount = inventoryList.items
            .map { it.productCode }
            .distinct()
            .size

        if (inventoryList.productCount != calculatedProductCount) {
            return false
        }

        // Toplam adet gerçek ürün satırlarıyla aynı olmalıdır.
        val calculatedTotalQuantity = inventoryList.items.sumOf {
            it.quantity
        }

        if (inventoryList.totalQuantity != calculatedTotalQuantity) {
            return false
        }

        return true
    }
}