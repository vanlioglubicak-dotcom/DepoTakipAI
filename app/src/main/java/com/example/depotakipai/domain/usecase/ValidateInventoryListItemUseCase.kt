package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListItem

class ValidateInventoryListItemUseCase {

    operator fun invoke(
        item: InventoryListItem
    ): Boolean {

        val productCode = item.productCode.trim()
        val color = item.color.trim()
        val size = item.size.trim()

        // Temel alanlar boş bırakılamaz.
        if (productCode.isEmpty()) {
            return false
        }

        if (color.isEmpty()) {
            return false
        }

        if (size.isEmpty()) {
            return false
        }

        // Adet sıfır veya negatif olamaz.
        if (item.quantity <= 0) {
            return false
        }

        // Ürün kodunun marka öneki korunmalıdır.
        val normalizedCode = productCode.uppercase()

        val hasValidBrandPrefix =
            normalizedCode.startsWith("SNZ-") ||
                    normalizedCode.startsWith("MTS-") ||
                    normalizedCode.startsWith("FS-")

        if (!hasValidBrandPrefix) {
            return false
        }

        return true
    }
}