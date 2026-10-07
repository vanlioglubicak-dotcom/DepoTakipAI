package com.example.depotakipai.data.catalog

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs

/**
 * Katalog ürün görselinde recolor yapılabilecek alanı
 * belirlemek için kullanılacak maske yardımcıları.
 *
 * ÖNEMLİ:
 * Bu sınıf doğrudan bütün resmi boyamaz.
 * Arka plan, beyaz zemin ve belirgin aksesuarları
 * mümkün olduğunca maske dışında bırakır.
 */
object CatalogGarmentMask {

    data class MaskResult(
        val mask: Bitmap,
        val coverage: Float
    )

    /**
     * Bitmap üzerinde kontrollü bir kıyafet maskesi oluşturur.
     *
     * Bu ilk katman güvenli bir heuristik maskedir.
     * Gerçek AI/ML segmentasyon daha sonra bu sınıfın içine
     * eklenebilir.
     */
    fun createMask(
        source: Bitmap,
        backgroundTolerance: Int = 18
    ): MaskResult {

        val width = source.width
        val height = source.height

        val mask = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ALPHA_8
        )

        var selectedPixels = 0
        val totalPixels = width * height

        for (y in 0 until height) {
            for (x in 0 until width) {

                val pixel = source.getPixel(x, y)

                val alpha = Color.alpha(pixel)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                val isTransparent =
                    alpha < 20

                val isNearWhite =
                    red >= 255 - backgroundTolerance &&
                            green >= 255 - backgroundTolerance &&
                            blue >= 255 - backgroundTolerance

                val isNearGrayBackground =
                    abs(red - green) <= 5 &&
                            abs(green - blue) <= 5 &&
                            red >= 220

                val shouldExclude =
                    isTransparent ||
                            isNearWhite ||
                            isNearGrayBackground

                val maskAlpha =
                    if (shouldExclude) {
                        0
                    } else {
                        255
                    }

                if (maskAlpha > 0) {
                    selectedPixels++
                }

                mask.setPixel(
                    x,
                    y,
                    Color.argb(
                        maskAlpha,
                        255,
                        255,
                        255
                    )
                )
            }
        }

        val coverage =
            if (totalPixels > 0) {
                selectedPixels.toFloat() / totalPixels.toFloat()
            } else {
                0f
            }

        return MaskResult(
            mask = mask,
            coverage = coverage
        )
    }

    /**
     * Masked alanın güvenli bir seviyede olup olmadığını kontrol eder.
     *
     * Çok küçük maske:
     * ürün bulunamadı kabul edilir.
     *
     * Çok büyük maske:
     * arka planın da seçilmiş olma ihtimali vardır.
     */
    fun isUsable(
        result: MaskResult
    ): Boolean {
        return result.coverage in 0.03f..0.80f
    }

    /**
     * Maskeyi yumuşatmak için basit kenar yumuşatma.
     *
     * Amaç ürünün kenarlarında sert renk geçişlerini azaltmaktır.
     */
    fun soften(
        mask: Bitmap,
        radius: Int = 2
    ): Bitmap {

        if (radius <= 0) {
            return mask.copy(
                Bitmap.Config.ALPHA_8,
                false
            )
        }

        val width = mask.width
        val height = mask.height

        val result = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ALPHA_8
        )

        for (y in 0 until height) {
            for (x in 0 until width) {

                var total = 0
                var count = 0

                for (dy in -radius..radius) {
                    for (dx in -radius..radius) {

                        val nx = x + dx
                        val ny = y + dy

                        if (
                            nx >= 0 &&
                            nx < width &&
                            ny >= 0 &&
                            ny < height
                        ) {
                            total += Color.alpha(
                                mask.getPixel(nx, ny)
                            )
                            count++
                        }
                    }
                }

                val average =
                    if (count > 0) {
                        total / count
                    } else {
                        0
                    }

                result.setPixel(
                    x,
                    y,
                    Color.argb(
                        average,
                        255,
                        255,
                        255
                    )
                )
            }
        }

        return result
    }
}