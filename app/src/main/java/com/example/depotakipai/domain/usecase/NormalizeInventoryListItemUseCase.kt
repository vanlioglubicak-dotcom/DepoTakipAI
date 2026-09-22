package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListItem

class NormalizeInventoryListItemUseCase(
    private val normalizeProductCode: NormalizeInventoryProductCodeUseCase,
    private val generateSystemBarcode: GenerateInventorySystemBarcodeUseCase
) {

    operator fun invoke(
        item: InventoryListItem
    ): InventoryListItem? {

        val normalizedCode = normalizeProductCode(
            item.productCode
        ) ?: return null

        val generatedBarcode = generateSystemBarcode(
            normalizedCode
        )

        val color = item.color
            .trim()
            .uppercase()

        val size = item.size
            .trim()
            .uppercase()

        if (color.isEmpty()) {
            return null
        }

        if (size.isEmpty()) {
            return null
        }

        if (item.quantity <= 0) {
            return null
        }

        return item.copy(
            productCode = normalizedCode,
            systemBarcode = generatedBarcode,
            color = color,
            size = size
        )
    }
}