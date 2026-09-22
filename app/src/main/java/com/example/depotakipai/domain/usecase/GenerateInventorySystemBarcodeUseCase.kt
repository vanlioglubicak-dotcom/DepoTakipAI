package com.example.depotakipai.domain.usecase

class GenerateInventorySystemBarcodeUseCase {

    operator fun invoke(
        productCode: String
    ): String? {

        val value = productCode
            .trim()
            .uppercase()

        if (value.isEmpty()) {
            return null
        }

        val numericPart = value
            .substringAfterLast("-")
            .filter { it.isDigit() }

        if (numericPart.isEmpty()) {
            return null
        }

        // Sistem barkodu her zaman 8 haneli olmalıdır.
        if (numericPart.length > 8) {
            return null
        }

        return numericPart.padStart(8, '0')
    }
}