package com.example.depotakipai.data.camera

import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult

class IncomingReturnBarcodeAnalyzer {

    fun analyze(
        barcode: String,
        currentResult: IncomingReturnAnalysisResult? = null
    ): IncomingReturnAnalysisResult {

        val normalizedBarcode = barcode
            .trim()
            .replace(" ", "")

        /*
         * Trendyol / üretici barkodu:
         * 85 ile başlayan barkodlar sistem barkodu değildir.
         */
        if (normalizedBarcode.startsWith("85")) {
            return currentResult?.copy(
                requiresManualReview = true,
                isValid = currentResult.productCode != null
            ) ?: IncomingReturnAnalysisResult(
                rawText = normalizedBarcode,
                confidence = 1f,
                isValid = false,
                requiresManualReview = true
            )
        }

        /*
         * Sistem barkodu 8 haneli olmalıdır.
         */
        if (!normalizedBarcode.matches(Regex("""\d{8}"""))) {
            return currentResult?.copy(
                requiresManualReview = true,
                isValid = currentResult.productCode != null
            ) ?: IncomingReturnAnalysisResult(
                rawText = normalizedBarcode,
                confidence = 1f,
                isValid = false,
                requiresManualReview = true
            )
        }

        /*
         * 8 haneli sistem barkodu bulundu.
         * Ürün kodunun sayısal kısmı sistem barkodundan çıkarılır.
         *
         * 00002926 -> 2926
         * 00002770 -> 2770
         */
        val numericProductCode = normalizedBarcode
            .trimStart('0')
            .ifEmpty { "0" }

        val existingProductCode = currentResult?.productCode

        val productCode = when {
            existingProductCode != null &&
                    existingProductCode.matches(
                        Regex("""(?:SNZ|MTS|FS)-\d+""")
                    ) -> existingProductCode

            else -> numericProductCode
        }

        return IncomingReturnAnalysisResult(
            productCode = productCode,
            systemBarcode = normalizedBarcode,
            color = currentResult?.color,
            size = currentResult?.size,
            quantity = currentResult?.quantity,
            source = currentResult?.source,
            photoUri = currentResult?.photoUri,
            confidence = 1f,
            rawText = buildString {
                append(currentResult?.rawText.orEmpty())

                if (isNotEmpty()) {
                    append("\n")
                }

                append("Sistem Barkodu: ")
                append(normalizedBarcode)
            },
            isValid = true,
            requiresManualReview =
                currentResult?.color == null ||
                        currentResult.size == null ||
                        currentResult.quantity == null
        )
    }

    fun isManufacturerBarcode(
        barcode: String
    ): Boolean {
        return barcode
            .trim()
            .replace(" ", "")
            .startsWith("85")
    }

    fun isValidSystemBarcode(
        barcode: String
    ): Boolean {
        val normalizedBarcode = barcode
            .trim()
            .replace(" ", "")

        return normalizedBarcode.matches(
            Regex("""\d{8}""")
        ) && !normalizedBarcode.startsWith("85")
    }

    fun createSystemBarcode(
        productCode: String
    ): String? {

        val numericPart = Regex("""\d+""")
            .find(productCode)
            ?.value
            ?: return null

        if (numericPart.length > 8) {
            return null
        }

        return numericPart.padStart(8, '0')
    }
}