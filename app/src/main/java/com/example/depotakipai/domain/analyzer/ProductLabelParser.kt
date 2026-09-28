package com.example.depotakipai.domain.analyzer

data class ProductLabelData(
    val productCode: String = "",
    val systemBarcode: String = "",
    val size: String = "",
    val color: String = "",
    val quantity: Int = 0
)

object ProductLabelParser {

    fun parse(rawText: String): ProductLabelData {

        val lines = rawText
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        var productCode = ""
        var size = ""
        var color = ""
        var quantity = 0

        for (line in lines) {

            val upperLine = line.uppercase()

            // -------------------------------------------------
            // ÜRÜN KODU
            // Örnek: SNZ-2926
            // -------------------------------------------------

            if (
                upperLine.contains("SNZ-") ||
                upperLine.contains("ÜRÜN KODU") ||
                upperLine.contains("URUN KODU")
            ) {
                val value = line
                    .substringAfter(":")
                    .trim()

                if (value.isNotEmpty()) {
                    productCode = value
                }
            }

            // -------------------------------------------------
            // BEDEN
            // Örnek: BEDEN: L
            // -------------------------------------------------

            if (
                upperLine.startsWith("BEDEN") ||
                upperLine.startsWith("SIZE")
            ) {
                size = line
                    .substringAfter(":")
                    .trim()
            }

            // -------------------------------------------------
            // RENK
            // Örnek: RENK: SİYAH
            // -------------------------------------------------

            if (
                upperLine.startsWith("RENK") ||
                upperLine.startsWith("COLOR")
            ) {
                color = line
                    .substringAfter(":")
                    .trim()
            }

            // -------------------------------------------------
            // ADET
            // Örnek: ADET: 3
            // -------------------------------------------------

            if (
                upperLine.startsWith("ADET") ||
                upperLine.startsWith("MIKTAR") ||
                upperLine.startsWith("MİKTAR") ||
                upperLine.startsWith("QUANTITY")
            ) {
                quantity = extractNumber(line)
            }

            // -------------------------------------------------
            // SNZ KODU SERBEST SATIRDA GELİRSE
            // -------------------------------------------------

            if (
                productCode.isEmpty() &&
                upperLine.matches(Regex("SNZ-\\d+.*"))
            ) {
                productCode = line.split(" ").first()
            }
        }

        // -----------------------------------------------------
        // SİSTEM BARKODU
        //
        // SNZ-2926
        //       ↓
        // 00002926
        //
        // Gerçek 85... barkodu kullanılmaz.
        // -----------------------------------------------------

        val systemBarcode = createSystemBarcode(productCode)

        return ProductLabelData(
            productCode = productCode,
            systemBarcode = systemBarcode,
            size = size,
            color = color,
            quantity = quantity
        )
    }

    private fun createSystemBarcode(productCode: String): String {

        val numberPart = productCode
            .filter { it.isDigit() }

        if (numberPart.isEmpty()) {
            return ""
        }

        return numberPart
            .takeLast(8)
            .padStart(8, '0')
    }

    private fun extractNumber(text: String): Int {

        return Regex("""\d+""")
            .find(text)
            ?.value
            ?.toIntOrNull()
            ?: 0
    }
}