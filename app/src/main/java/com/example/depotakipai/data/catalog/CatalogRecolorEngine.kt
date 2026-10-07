package com.example.depotakipai.data.catalog

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.Color as ComposeColor
import kotlin.math.roundToInt

/**
 * Katalog ürün görseli için kontrollü renk değiştirme motoru.
 *
 * Arka planın tamamını boyamak yerine yalnızca
 * CatalogGarmentMask tarafından belirlenen alan üzerinde çalışır.
 *
 * Işık ve gölge bilgisini korumaya çalışır.
 */
object CatalogRecolorEngine {

    fun recolor(
        source: Bitmap,
        targetColor: ComposeColor,
        mask: Bitmap
    ): Bitmap {

        require(
            source.width == mask.width &&
                    source.height == mask.height
        ) {
            "Kaynak görsel ile maske boyutları aynı olmalıdır."
        }

        val result = source.copy(
            Bitmap.Config.ARGB_8888,
            true
        )

        val targetRed =
            (targetColor.red * 255f).coerceIn(0f, 255f)

        val targetGreen =
            (targetColor.green * 255f).coerceIn(0f, 255f)

        val targetBlue =
            (targetColor.blue * 255f).coerceIn(0f, 255f)

        for (y in 0 until source.height) {
            for (x in 0 until source.width) {

                val sourcePixel =
                    source.getPixel(x, y)

                val maskPixel =
                    mask.getPixel(x, y)

                val maskAlpha =
                    Color.alpha(maskPixel)

                if (maskAlpha <= 0) {
                    continue
                }

                val sourceAlpha =
                    Color.alpha(sourcePixel)

                if (sourceAlpha <= 0) {
                    continue
                }

                val red =
                    Color.red(sourcePixel).toFloat()

                val green =
                    Color.green(sourcePixel).toFloat()

                val blue =
                    Color.blue(sourcePixel).toFloat()

                /*
                 * Kaynak görüntünün parlaklığını koruyoruz.
                 *
                 * Böylece kumaşın:
                 * - gölgeleri
                 * - kıvrımları
                 * - ışık alan bölgeleri
                 *
                 * tamamen düz bir renge dönüşmez.
                 */
                val luminance =
                    (
                            red * 0.2126f +
                                    green * 0.7152f +
                                    blue * 0.0722f
                            ) / 255f

                /*
                 * Çok karanlık bölgelerin tamamen siyaha
                 * düşmesini engelliyoruz.
                 */
                val safeLuminance =
                    luminance.coerceIn(
                        0.15f,
                        1.0f
                    )

                val recoloredRed =
                    (
                            targetRed * safeLuminance
                            ).coerceIn(
                            0f,
                            255f
                        )

                val recoloredGreen =
                    (
                            targetGreen * safeLuminance
                            ).coerceIn(
                            0f,
                            255f
                        )

                val recoloredBlue =
                    (
                            targetBlue * safeLuminance
                            ).coerceIn(
                            0f,
                            255f
                        )

                /*
                 * Maske kenarlarında yumuşak geçiş.
                 */
                val blendAmount =
                    maskAlpha / 255f

                val finalRed =
                    red * (1f - blendAmount) +
                            recoloredRed * blendAmount

                val finalGreen =
                    green * (1f - blendAmount) +
                            recoloredGreen * blendAmount

                val finalBlue =
                    blue * (1f - blendAmount) +
                            recoloredBlue * blendAmount

                result.setPixel(
                    x,
                    y,
                    Color.argb(
                        sourceAlpha,
                        finalRed.roundToInt().coerceIn(0, 255),
                        finalGreen.roundToInt().coerceIn(0, 255),
                        finalBlue.roundToInt().coerceIn(0, 255)
                    )
                )
            }
        }

        return result
    }

    /**
     * Önce maske oluşturur, sonra güvenliyse recolor yapar.
     *
     * Maske güvenilir değilse kaynak görsel bozulmadan
     * aynen kopyalanır.
     */
    fun recolorSafely(
        source: Bitmap,
        targetColor: ComposeColor
    ): Bitmap {

        val maskResult =
            CatalogGarmentMask.createMask(
                source = source
            )

        if (!CatalogGarmentMask.isUsable(maskResult)) {
            return source.copy(
                Bitmap.Config.ARGB_8888,
                true
            )
        }

        val softenedMask =
            CatalogGarmentMask.soften(
                mask = maskResult.mask,
                radius = 2
            )

        return recolor(
            source = source,
            targetColor = targetColor,
            mask = softenedMask
        )
    }
}