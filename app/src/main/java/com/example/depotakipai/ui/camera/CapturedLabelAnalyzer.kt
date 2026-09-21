package com.example.depotakipai.ui.camera

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale

class CapturedLabelAnalyzer {

    private val recognizer =
        TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

    fun analyze(
        bitmap: Bitmap,
        onResult: (CameraScanResult) -> Unit,
        onError: (String) -> Unit
    ) {

        val image =
            InputImage.fromBitmap(
                bitmap,
                0
            )

        recognizer.process(image)

            .addOnSuccessListener { visionText ->

                val rawText =
                    visionText.text
                        .uppercase(Locale("tr", "TR"))
                        .trim()

                val productCode =
                    findProductCode(rawText)

                val color =
                    findColor(rawText)

                val size =
                    findSize(rawText)

                val numericPart =
                    productCode
                        ?.let {
                            Regex("\\d+")
                                .find(it)
                                ?.value
                        }

                /*
                 * Sistem barkodu:
                 *
                 * SNZ-2770
                 *      ↓
                 * 2770
                 *      ↓
                 * 00002770
                 *
                 * 85 ile başlayan üretici barkodu
                 * kesinlikle kullanılmaz.
                 */

                val systemBarcode =
                    numericPart
                        ?.takeLast(8)
                        ?.padStart(8, '0')

                val result =
                    CameraScanResult(

                        productCode =
                            productCode,

                        systemBarcode =
                            systemBarcode,

                        color =
                            color,

                        size =
                            size,

                        rawText =
                            rawText,

                        hasValidSystemBarcode =
                            !systemBarcode.isNullOrBlank()
                    )

                /*
                 * En azından ürün kodu veya
                 * renk/beden bilgisi bulunduysa
                 * sonucu başarılı kabul ediyoruz.
                 */

                val hasUsefulInformation =
                    !productCode.isNullOrBlank() ||
                            !color.isNullOrBlank() ||
                            !size.isNullOrBlank()

                if (hasUsefulInformation) {

                    onResult(result)

                } else {

                    onError(
                        "Etiket bilgileri okunamadı. " +
                                "Lütfen etiketi daha net çekin."
                    )
                }
            }

            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Etiket analiz edilemedi."
                )
            }
    }

    // =========================================================
    // ÜRÜN KODU
    // =========================================================

    private fun findProductCode(
        text: String
    ): String? {

        /*
         * Öncelik:
         *
         * SNZ-2770
         * SNZ – 2770
         * SNZ 2770
         */

        val prefixedPattern =
            Regex(
                pattern =
                    """\bSNZ\s*[-–—]?\s*(\d{3,8})\b""",
                option =
                    RegexOption.IGNORE_CASE
            )

        val prefixedMatch =
            prefixedPattern.find(text)

        if (prefixedMatch != null) {

            val number =
                prefixedMatch
                    .groupValues[1]

            return "SNZ-$number"
        }

        /*
         * Bazı etiketlerde sadece:
         *
         * 2770
         *
         * bulunabilir.
         *
         * Fakat 85 ile başlayan uzun
         * üretici barkodlarını ürün kodu
         * olarak kabul etmiyoruz.
         */

        val numberPattern =
            Regex(
                pattern =
                    """(?<!\d)(\d{3,8})(?!\d)"""
            )

        val candidates =
            numberPattern
                .findAll(text)
                .map {
                    it.value
                }
                .filter {
                    !it.startsWith("85")
                }
                .toList()

        /*
         * Etiketteki 2770 gibi kısa ürün kodunu
         * bulmaya çalış.
         */

        val productNumber =
            candidates.firstOrNull {
                it.length in 3..6
            }

        if (productNumber != null) {

            return "SNZ-$productNumber"
        }

        return null
    }

    // =========================================================
    // RENK
    // =========================================================

    private fun findColor(
        text: String
    ): String? {

        val colorPattern =
            Regex(
                pattern =
                    """RENK\s*[:\-]?\s*([A-ZÇĞİÖŞÜ ]+)""",
                option =
                    RegexOption.IGNORE_CASE
            )

        val match =
            colorPattern.find(text)

        if (match != null) {

            return match
                .groupValues[1]
                .trim()
                .replace(
                    Regex("\\s+"),
                    " "
                )
        }

        /*
         * Etiket yapısına göre yaygın renkleri
         * ayrıca kontrol ediyoruz.
         */

        val knownColors =
            listOf(
                "SİYAH",
                "BEYAZ",
                "KIRMIZI",
                "LACİVERT",
                "MAVİ",
                "YEŞİL",
                "GRİ",
                "GRI",
                "BEJ",
                "KAHVE",
                "KAHVERENGİ",
                "BORDO",
                "PEMBE",
                "MOR",
                "TURUNCU",
                "SARI"
            )

        return knownColors.firstOrNull {
            text.contains(it)
        }
    }

    // =========================================================
    // BEDEN
    // =========================================================

    private fun findSize(
        text: String
    ): String? {

        val sizePattern =
            Regex(
                pattern =
                    """BEDEN\s*[:\-]?\s*(XS|XXS|S|M|L|XL|XXL|XXXL|\d{1,3})\b""",
                option =
                    RegexOption.IGNORE_CASE
            )

        val match =
            sizePattern.find(text)

        if (match != null) {

            return match
                .groupValues[1]
                .uppercase()
        }

        /*
         * Etiket OCR'da BEDEN kelimesini
         * kaçırırsa bilinen bedenleri ara.
         */

        val knownSizes =
            listOf(
                "XXXL",
                "XXL",
                "XXS",
                "XL",
                "XS",
                "S",
                "M",
                "L"
            )

        return knownSizes.firstOrNull {
            Regex(
                """\b${Regex.escape(it)}\b"""
            ).containsMatchIn(text)
        }
    }
}