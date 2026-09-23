package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.ShelfPosition
import com.example.depotakipai.domain.model.Stock
import com.example.depotakipai.domain.repository.StockRepository

class UpdateStockUseCase(
    private val repository: StockRepository
) {

    suspend operator fun invoke(
        productCode: String,
        systemBarcode: String?,
        color: String,
        size: String,
        quantity: Int,
        rowNumber: Int?,
        shelfNumber: String?,
        position: ShelfPosition?
    ): Boolean {

        val normalizedCode = productCode
            .trim()
            .uppercase()

        val normalizedColor = color
            .trim()
            .uppercase()

        val normalizedSize = size
            .trim()
            .uppercase()

        if (normalizedCode.isEmpty()) return false
        if (normalizedColor.isEmpty()) return false
        if (normalizedSize.isEmpty()) return false
        if (quantity < 0) return false

        val existingStock = repository.getStockByProductCode(
            normalizedCode
        ) ?: return false

        val updatedStock = Stock(
            productCode = normalizedCode,
            systemBarcode = systemBarcode
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?: existingStock.systemBarcode,
            color = normalizedColor,
            size = normalizedSize,
            quantity = quantity,
            rowNumber = rowNumber,
            shelfNumber = shelfNumber?.trim(),
            position = position,
            updatedAt = System.currentTimeMillis()
        )

        repository.updateStock(updatedStock)

        return true
    }
}