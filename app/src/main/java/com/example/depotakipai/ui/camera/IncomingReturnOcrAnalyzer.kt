package com.example.depotakipai.data.camera

import android.graphics.Bitmap
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult
import com.example.depotakipai.domain.model.ReturnSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class IncomingReturnOcrAnalyzer {

    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    suspend fun analyze(
        bitmap: Bitmap,
        source: ReturnSource? = null,
        photoUri: String? = null
    ): IncomingReturnAnalysisResult {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        val recognizedText = recognizeText(inputImage)

        return parseResult(
            text = recognizedText,
            source = source,
            photoUri = photoUri
        )
    }

    private suspend fun recognizeText(
        image: InputImage
    ): String = suspendCancellableCoroutine { continuation ->

        recognizer.process(image)
            .addOnSuccessListener { result ->
                if (continuation.isActive) {
                    continuation.resume(result.text)
                }
            }
            .addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resume("")
                }
            }
    }

    private fun parseResult(
        text: String,
        source: ReturnSource?,
        photoUri: String?
    ): IncomingReturnAnalysisResult {

        val normalizedText = text
            .replace("–", "-")
            .replace("—", "-")
            .replace("\n\n", "\n")
            .trim()

        val productCode = findProductCode(normalizedText)
        val color = findColor(normalizedText)
        val size = findSize(normalizedText)

        val systemBarcode = productCode
            ?.let { createSystemBarcode(it) }

        val quantity = findQuantity(normalizedText)

        val hasUsefulInformation =
            productCode != null ||
                    color != null ||
                    size != null

        val requiresManualReview =
            productCode == null ||
                    color == null ||
                    size == null ||
                    quantity == null

        return IncomingReturnAnalysisResult(
            productCode = productCode,
            systemBarcode = systemBarcode,
            color = color,
            size = size,
            quantity = quantity,
            source = source,
            photoUri = photoUri,
            confidence = if (hasUsefulInformation) 0.75f else 0f,
            rawText = normalizedText,
            isValid = hasUsefulInformation,
            requiresManualReview = requiresManualReview
        )
    }

    private fun findProductCode(
        text: String
    ): String? {

        val brandPattern = Regex(
            pattern = """\b(SNZ|MTS|FS)\s*[-:]?\s*(\d{3,6})\b""",
            option = RegexOption.IGNORE_CASE
        )

        val brandMatch = brandPattern.find(text)

        if (brandMatch != null) {
            val brand = brandMatch.groupValues[1]
                .uppercase()

            val number = brandMatch.groupValues[2]

            return "$brand-$number"
        }

        val productLinePattern = Regex(
            pattern = """(?im)^\s*(?:ÜRÜN\s*KODU|ÜRÜN|MODEL)\s*[:\-]?\s*(\d{3,6})\s*$"""
        )

        val productLineMatch =
            productLinePattern.find(text)

        if (productLineMatch != null) {
            return productLineMatch
                .groupValues[1]
        }

        val plainCodePattern = Regex(
            pattern = """(?<!\d)\d{3,6}(?!\d)"""
        )

        val plainCode = plainCodePattern
            .find(text)
            ?.value

        return plainCode
    }

    private fun findColor(
        text: String
    ): String? {

        val colorPattern = Regex(
            pattern = """(?im)(?:RENK|COLOR)\s*[:\-]?\s*([^\n\r]+)"""
        )

        val match = colorPattern.find(text)

        return match
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun findSize(
        text: String
    ): String? {

        val sizePattern = Regex(
            pattern = """(?im)(?:BEDEN|SIZE)\s*[:\-]?\s*([A-Z0-9ÇĞİÖŞÜ+\-]+)"""
        )

        val match = sizePattern.find(text)

        return match
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
            ?.uppercase()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun findQuantity(
        text: String
    ): Int? {

        val quantityPattern = Regex(
            pattern = """(?im)(?:ADET|QTY|QUANTITY)\s*[:\-]?\s*(\d+)"""
        )

        val match = quantityPattern.find(text)

        return match
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()
            ?.takeIf { it > 0 }
    }

    private fun createSystemBarcode(
        productCode: String
    ): String? {

        val numericPart = Regex(
            pattern = """\d+"""
        )
            .find(productCode)
            ?.value
            ?: return null

        if (numericPart.length > 8) {
            return null
        }

        return numericPart
            .padStart(8, '0')
    }

    fun close() {
        recognizer.close()
    }
}