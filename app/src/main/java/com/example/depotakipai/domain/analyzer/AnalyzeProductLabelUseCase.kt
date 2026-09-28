package com.example.depotakipai.domain.analyzer

class AnalyzeProductLabelUseCase {

    operator fun invoke(rawText: String): ProductAnalysisResult {

        val parsed = ProductLabelParser.parse(rawText)

        return ProductAnalysisResult(
            productCode = parsed.productCode,
            systemBarcode = parsed.systemBarcode,
            size = parsed.size,
            color = parsed.color,
            quantity = parsed.quantity,
            rawText = rawText,
            confidence = calculateConfidence(parsed)
        )
    }

    private fun calculateConfidence(
        data: ProductLabelData
    ): Float {

        var score = 0f

        if (data.productCode.isNotBlank()) {
            score += 0.30f
        }

        if (data.systemBarcode.isNotBlank()) {
            score += 0.25f
        }

        if (data.size.isNotBlank()) {
            score += 0.15f
        }

        if (data.color.isNotBlank()) {
            score += 0.15f
        }

        if (data.quantity > 0) {
            score += 0.15f
        }

        return score.coerceIn(0f, 1f)
    }
}