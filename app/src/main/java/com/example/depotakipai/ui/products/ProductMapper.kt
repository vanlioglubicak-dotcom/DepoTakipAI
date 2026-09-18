package com.example.depotakipai.ui.products

import com.example.depotakipai.domain.model.Product
import com.example.depotakipai.domain.model.ShelfPosition

fun ProductFormState.toProduct(): Product {

    val cleanProductCode = productCode.trim()

    val cleanSystemBarcode =
        systemBarcode.trim().takeIf { it.isNotEmpty() }

    val cleanColor = color.trim()

    val cleanSize = size.trim()

    val parsedQuantity =
        quantity.trim()
            .toIntOrNull()
            ?.coerceAtLeast(0)
            ?: 0

    val parsedRowNumber =
        rowNumber.trim().toIntOrNull()

    val parsedShelfNumber =
        shelfNumber.trim().toIntOrNull()

    val parsedPosition =
        when (position.trim().uppercase()) {

            "ÖN",
            "ON",
            "FRONT" -> ShelfPosition.FRONT

            "ARKA",
            "BACK" -> ShelfPosition.BACK

            else -> null
        }

    return Product(
        productCode = cleanProductCode,
        systemBarcode = cleanSystemBarcode,
        color = cleanColor,
        size = cleanSize,
        quantity = parsedQuantity,
        rowNumber = parsedRowNumber,
        shelfNumber = parsedShelfNumber,
        position = parsedPosition,
        photoUri = photoUri,
        createdAt = System.currentTimeMillis()
    )
}