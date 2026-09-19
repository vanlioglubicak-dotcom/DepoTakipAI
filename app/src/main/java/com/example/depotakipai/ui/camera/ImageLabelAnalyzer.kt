package com.example.depotakipai.ui.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class CameraAnalyzer(
    private val onResult: (CameraScanResult) -> Unit
) : ImageAnalysis.Analyzer {

    private val barcodeScanner: BarcodeScanner =
        BarcodeScanning.getClient()

    private val textRecognizer =
        TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

    private val isProcessing = AtomicBoolean(false)

    override fun analyze(imageProxy: ImageProxy) {

        if (!isProcessing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image

        if (mediaImage == null) {
            isProcessing.set(false)
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        val barcodeTask = barcodeScanner.process(inputImage)
        val textTask = textRecognizer.process(inputImage)

        Tasks.whenAllSuccess<Any>(
            barcodeTask,
            textTask
        ).addOnSuccessListener { results ->

            try {

                val barcodes =
                    results.getOrNull(0) as? List<*>
                        ?: emptyList<Any>()

                val recognizedText =
                    results.getOrNull(1)?.let {
                        extractTextResult(it)
                    } ?: ""

                val result =
                    parseResult(
                        barcodes = barcodes,
                        rawText = recognizedText
                    )

                if (
                    result.productCode != null ||
                    result.color != null ||
                    result.size != null ||
                    result.systemBarcode != null
                ) {
                    onResult(result)
                }

            } finally {

                imageProxy.close()
                isProcessing.set(false)
            }

        }.addOnFailureListener {

            imageProxy.close()
            isProcessing.set(false)
        }
    }

    private fun extractTextResult(
        result: Any
    ): String {

        return try {

            val textResult =
                result as com.google.mlkit.vision.text.Text

            textResult.text

        } catch (_: Exception) {

            ""
        }
    }

    private fun parseResult(
        barcodes: List<*>,
        rawText: String
    ): CameraScanResult {

        val normalizedText =
            rawText
                .replace("\r", "\n")
                .replace("\t", " ")
                .trim()

        val productCode =
            findProductCode(normalizedText)

        val color =
            findColor(normalizedText)

        val size =
            findSize(normalizedText)

        val barcodeFromImage =
            findValidSystemBarcode(barcodes)

        val systemBarcode =
            barcodeFromImage
                ?: productCode
                    ?.let { createSystemBarcode(it) }

        return CameraScanResult(
            productCode = productCode,
            systemBarcode = systemBarcode,
            color = color,
            size = size,
            rawText = normalizedText,
            hasValidSystemBarcode = systemBarcode != null
        )
    }

    /**
     * Trendyol ürün kodunu bulur.
     *
     * Örnek:
     * SNZ-2926
     * SNZ2926
     * SNZ 2926
     *
     * Sonuç:
     * SNZ-2926
     */
    private fun findProductCode(
        text: String
    ): String? {

        val pattern =
            Regex(
                pattern = """\b([A-Za-zÇĞİÖŞÜçğıöşü]{2,6})[\s-]*(\d{3,8})\b"""
            )

        val match =
            pattern.find(text)

        if (match != null) {

            val prefix =
                match.groupValues[1]
                    .uppercase(Locale("tr", "TR"))

            val number =
                match.groupValues[2]

            return "$prefix-$number"
        }

        /*
         * Bazı ürünlerde sadece sayı bulunabilir.
         *
         * Örnek:
         * 2837
         *
         * Bu durumda bunu ürün kodu olarak kabul ediyoruz.
         *
         * 13 haneli üretici/trendyol barkodlarını
         * burada ürün kodu olarak kabul etmiyoruz.
         */
        val numberOnlyPattern =
            Regex("""(?<!\d)\d{3,8}(?!\d)""")

        val numberMatch =
            numberOnlyPattern.find(text)

        if (numberMatch != null) {

            val number =
                numberMatch.value

            /*
             * 85 ile başlayan değerleri
             * sistem barkodu olarak kullanmıyoruz.
             */
            if (!number.startsWith("85")) {
                return number
            }
        }

        return null
    }

    /**
     * Etikette:
     *
     * RENK: SİYAH
     *
     * gibi bilgileri arar.
     */
    private fun findColor(
        text: String
    ): String? {

        val pattern =
            Regex(
                pattern = """(?im)\bRENK\s*[:\-]?\s*([A-Za-zÇĞİÖŞÜçğıöşü ]+)"""
            )

        val match =
            pattern.find(text)

        return match
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
            ?.uppercase(Locale("tr", "TR"))
            ?.takeIf { it.isNotBlank() }
    }

    /**
     * Etikette:
     *
     * BEDEN: L
     *
     * gibi bilgileri arar.
     */
    private fun findSize(
        text: String
    ): String? {

        val pattern =
            Regex(
                pattern = """(?im)\bBEDEN\s*[:\-]?\s*([A-Za-zÇĞİÖŞÜçğıöşü0-9]+)"""
            )

        val match =
            pattern.find(text)

        return match
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
            ?.uppercase(Locale("tr", "TR"))
            ?.takeIf { it.isNotBlank() }
    }

    /**
     * Kamera tarafından okunan barkodlar içerisinden
     * sistem için geçerli olanı bulur.
     *
     * 85 ile başlayan barkodlar KESİNLİKLE yok sayılır.
     *
     * Örnek:
     *
     * 8560451140201 -> YOK SAY
     *
     * 00002926 -> GEÇERLİ
     */
    private fun findValidSystemBarcode(
        barcodes: List<*>
    ): String? {

        for (item in barcodes) {

            val barcode =
                item as? com.google.mlkit.vision.barcode.common.Barcode
                    ?: continue

            val rawValue =
                barcode.rawValue
                    ?.trim()
                    ?: continue

            if (rawValue.startsWith("85")) {
                continue
            }

            if (
                rawValue.length == 8 &&
                rawValue.all { it.isDigit() }
            ) {
                return rawValue
            }
        }

        return null
    }

    /**
     * Trendyol ürün kodundan sistem barkodu üretir.
     *
     * SNZ-2926 -> 00002926
     * SNZ-2759 -> 00002759
     * 2837     -> 00002837
     */
    private fun createSystemBarcode(
        productCode: String
    ): String? {

        val digits =
            productCode
                .filter { it.isDigit() }

        if (digits.isBlank()) {
            return null
        }

        if (digits.length > 8) {
            return null
        }

        return digits.padStart(
            8,
            '0'
        )
    }
}