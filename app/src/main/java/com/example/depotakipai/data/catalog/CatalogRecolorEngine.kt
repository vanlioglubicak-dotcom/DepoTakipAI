package com.example.depotakipai.data.catalog

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.Color as ComposeColor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * ============================================================
 * KATALOG PROFESYONEL AI RECOLOR MOTORU
 * ============================================================
 *
 * AMAÇ:
 *
 * Katalog ürün görselinde yalnızca AI tarafından belirlenen
 * kıyafet alanının rengini değiştirmek.
 *
 * KORUNACAK ALANLAR:
 *
 * - Yüz
 * - Saç
 * - Ten
 * - El
 * - Bacak
 * - Ayakkabı
 * - Arka plan
 * - Zemin
 * - Kıyafet dışı aksesuarlar
 *
 * DEĞİŞECEK ALAN:
 *
 * - Kıyafet kumaşı
 *
 * RENK DÖNÜŞÜMÜ:
 *
 * Kaynak kumaşın:
 *
 * - ışığı
 * - gölgesi
 * - hacmi
 * - koyu/açık bölgeleri
 * - doğal kontrastı
 *
 * korunarak hedef renk uygulanır.
 *
 * MANUEL MASKE:
 *
 * YOK.
 *
 * Kullanıcıdan:
 *
 * - DÜZELT
 * - EKLE
 * - ÇIKAR
 *
 * istenmez.
 *
 * ============================================================
 */
object CatalogRecolorEngine {

    // ============================================================
    // ANA RECOLOR
    // ============================================================

    /**
     * AI tarafından oluşturulmuş maske içerisinde renk değiştirir.
     *
     * Bu fonksiyon maske dışındaki hiçbir piksele dokunmaz.
     */
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

        val result =
            source.copy(
                Bitmap.Config.ARGB_8888,
                true
            )

        if (
            source.width <= 0 ||
            source.height <= 0
        ) {
            return result
        }

        // --------------------------------------------------------
        // HEDEF RENK
        // --------------------------------------------------------

        val targetRed =
            (targetColor.red * 255f)
                .coerceIn(
                    0f,
                    255f
                )

        val targetGreen =
            (targetColor.green * 255f)
                .coerceIn(
                    0f,
                    255f
                )

        val targetBlue =
            (targetColor.blue * 255f)
                .coerceIn(
                    0f,
                    255f
                )

        // --------------------------------------------------------
        // HEDEF RENK HSV
        // --------------------------------------------------------

        val targetHsv =
            rgbToHsv(
                targetRed,
                targetGreen,
                targetBlue
            )

        val targetHue =
            targetHsv[0]

        val targetSaturation =
            targetHsv[1]

        val targetValue =
            targetHsv[2]

        // --------------------------------------------------------
        // ARKA PLAN PALETİ
        // --------------------------------------------------------

        val backgroundPalette =
            buildBackgroundPalette(
                source
            )

        // --------------------------------------------------------
        // GÖRSEL
        // --------------------------------------------------------

        for (
        y in 0 until source.height
        ) {

            for (
            x in 0 until source.width
            ) {

                val sourcePixel =
                    source.getPixel(
                        x,
                        y
                    )

                val maskPixel =
                    mask.getPixel(
                        x,
                        y
                    )

                val maskAlpha =
                    Color.alpha(
                        maskPixel
                    )

                // ------------------------------------------------
                // MASKE DIŞI
                // ------------------------------------------------

                if (
                    maskAlpha <= 0
                ) {
                    continue
                }

                val sourceAlpha =
                    Color.alpha(
                        sourcePixel
                    )

                if (
                    sourceAlpha <= 0
                ) {
                    continue
                }

                val red =
                    Color.red(
                        sourcePixel
                    ).toFloat()

                val green =
                    Color.green(
                        sourcePixel
                    ).toFloat()

                val blue =
                    Color.blue(
                        sourcePixel
                    ).toFloat()

                // ------------------------------------------------
                // ÇOK AÇIK ARKA PLAN KORUMASI
                // ------------------------------------------------
                //
                // Sadece çok güçlü beyaz arka planları korur.
                //
                // Beyaz kumaşı yanlışlıkla tamamen silmemek için
                // sınır oldukça yüksek tutulmuştur.

                if (
                    isVeryLightBackground(
                        red = red,
                        green = green,
                        blue = blue
                    )
                ) {
                    continue
                }

                // ------------------------------------------------
                // ARKA PLAN BENZERLİĞİ
                // ------------------------------------------------
                //
                // Burada eski sistemdeki kadar agresif değiliz.
                //
                // Çünkü AI maskesi zaten asıl korumayı yapıyor.
                //
                // Sadece neredeyse birebir arka plan pikselini
                // koruyoruz.

                if (
                    isStrongBackgroundMatch(
                        red = red,
                        green = green,
                        blue = blue,
                        backgroundPalette = backgroundPalette
                    )
                ) {
                    continue
                }

                // ------------------------------------------------
                // KAYNAK HSV
                // ------------------------------------------------

                val sourceHsv =
                    rgbToHsv(
                        red,
                        green,
                        blue
                    )

                val sourceValue =
                    sourceHsv[2]

                val sourceSaturation =
                    sourceHsv[1]

                // ------------------------------------------------
                // KUMAŞ LUMINANCE
                // ------------------------------------------------

                val luminance =
                    (
                            red * 0.2126f +
                                    green * 0.7152f +
                                    blue * 0.0722f
                            ) / 255f

                // ------------------------------------------------
                // PROFESYONEL KUMAŞ PARLAKLIK HARİTASI
                // ------------------------------------------------
                //
                // Eski sistem:
                //
                // target * luminance
                //
                // kullanıyordu.
                //
                // Bu özellikle siyah elbiselerde hedef rengi
                // neredeyse görünmez hale getiriyordu.
                //
                // Yeni sistem koyu kumaşa kontrollü bir taban
                // parlaklığı verir ve orijinal gölge bilgisini
                // bunun üzerine taşır.

                val fabricLightness =
                    calculateFabricLightness(
                        luminance = luminance,
                        sourceValue = sourceValue,
                        sourceSaturation = sourceSaturation
                    )

                // ------------------------------------------------
                // HEDEF RENK HSV
                // ------------------------------------------------
                //
                // Hedef rengin:
                //
                // H = hedef renk
                // S = hedef renk
                // V = kumaş ışığı
                //
                // kullanılır.

                val recolored =
                    hsvToRgb(
                        hue = targetHue,
                        saturation =
                            calculateTargetSaturation(
                                targetSaturation =
                                    targetSaturation,
                                sourceSaturation =
                                    sourceSaturation,
                                sourceValue =
                                    sourceValue
                            ),
                        value =
                            calculateTargetValue(
                                targetValue =
                                    targetValue,
                                fabricLightness =
                                    fabricLightness,
                                sourceValue =
                                    sourceValue
                            )
                    )

                // ------------------------------------------------
                // KUMAŞ DOKUSU KORUMA
                // ------------------------------------------------
                //
                // Çok koyu kumaşlarda yalnızca düz renk
                // uygulanmasını engeller.
                //
                // Kaynak luminance ile yeni luminance arasındaki
                // farkın küçük bir kısmı geri eklenir.

                val textureAdjusted =
                    preserveFabricTexture(
                        originalRed = red,
                        originalGreen = green,
                        originalBlue = blue,
                        recoloredRed = recolored[0],
                        recoloredGreen = recolored[1],
                        recoloredBlue = recolored[2]
                    )

                // ------------------------------------------------
                // MASKE ALFASI
                // ------------------------------------------------

                val blendAmount =
                    (maskAlpha / 255f)
                        .coerceIn(
                            0f,
                            1f
                        )

                // ------------------------------------------------
                // SON RENK
                // ------------------------------------------------

                val finalRed =
                    red * (
                            1f - blendAmount
                            ) +
                            textureAdjusted[0] *
                            blendAmount

                val finalGreen =
                    green * (
                            1f - blendAmount
                            ) +
                            textureAdjusted[1] *
                            blendAmount

                val finalBlue =
                    blue * (
                            1f - blendAmount
                            ) +
                            textureAdjusted[2] *
                            blendAmount

                result.setPixel(
                    x,
                    y,
                    Color.argb(
                        sourceAlpha,
                        finalRed
                            .roundToInt()
                            .coerceIn(
                                0,
                                255
                            ),
                        finalGreen
                            .roundToInt()
                            .coerceIn(
                                0,
                                255
                            ),
                        finalBlue
                            .roundToInt()
                            .coerceIn(
                                0,
                                255
                            )
                    )
                )
            }
        }

        return result
    }

    // ============================================================
    // GÜVENLİ AI RECOLOR
    // ============================================================

    /**
     * Önce yeni AI maskesi denenir.
     *
     * AI başarılı:
     *
     *     AI MASK
     *       ↓
     *   RECOLOR
     *
     * AI başarısız:
     *
     *     FALLBACK MASK
     *       ↓
     *   RECOLOR
     */
    suspend fun recolorSafely(
        source: Bitmap,
        targetColor: ComposeColor
    ): Bitmap {

        if (
            source.width <= 0 ||
            source.height <= 0
        ) {
            return source.copy(
                Bitmap.Config.ARGB_8888,
                true
            )
        }

        // --------------------------------------------------------
        // 1. AI MASK
        // --------------------------------------------------------

        val aiMaskResult =
            try {

                CatalogGarmentMask.createAiMask(
                    source = source
                )

            } catch (
                _: Exception
            ) {

                null
            }

        // --------------------------------------------------------
        // 2. AI MASK KULLAN
        // --------------------------------------------------------

        if (
            aiMaskResult != null &&
            CatalogGarmentMask.isUsable(
                aiMaskResult
            )
        ) {

            return recolor(
                source = source,
                targetColor = targetColor,
                mask = aiMaskResult.mask
            )
        }

        // --------------------------------------------------------
        // 3. FALLBACK
        // --------------------------------------------------------

        val fallbackMaskResult =
            CatalogGarmentMask.createMask(
                source = source
            )

        if (
            !CatalogGarmentMask.isUsable(
                fallbackMaskResult
            )
        ) {

            return source.copy(
                Bitmap.Config.ARGB_8888,
                true
            )
        }

        // --------------------------------------------------------
        // 4. FALLBACK KENAR YUMUŞATMA
        // --------------------------------------------------------

        val softenedMask =
            CatalogGarmentMask.soften(
                mask = fallbackMaskResult.mask,
                radius = 1
            )

        return recolor(
            source = source,
            targetColor = targetColor,
            mask = softenedMask
        )
    }

    // ============================================================
    // KUMAŞ PARLAKLIĞI
    // ============================================================

    /**
     * Koyu kumaşların hedef renkte görünür kalmasını sağlar.
     *
     * Örneğin:
     *
     * Siyah elbise
     * +
     * Pembe
     *
     * artık neredeyse siyah kalmaz.
     */
    private fun calculateFabricLightness(
        luminance: Float,
        sourceValue: Float,
        sourceSaturation: Float
    ): Float {

        val safeLuminance =
            luminance.coerceIn(
                0f,
                1f
            )

        val safeValue =
            sourceValue.coerceIn(
                0f,
                1f
            )

        val safeSaturation =
            sourceSaturation.coerceIn(
                0f,
                1f
            )

        /*
         * Koyu kumaşlarda kontrollü taban ışığı.
         *
         * Tam siyah:
         *
         * 0.00
         *
         * doğrudan kullanılmaz.
         *
         * Bunun yerine:
         *
         * 0.34
         *
         * civarında görünürlük başlatılır.
         */

        val lifted =
            0.34f +
                    safeLuminance * 0.66f

        /*
         * Kumaşın orijinal saturation bilgisi çok düşükse
         * (siyah/gri gibi), hedef rengin görünürlüğünü biraz
         * daha artır.
         */

        val darkFabricBoost =
            if (
                safeSaturation < 0.20f &&
                safeValue < 0.35f
            ) {
                1.08f
            } else {
                1.0f
            }

        return (
                lifted *
                        darkFabricBoost
                )
            .coerceIn(
                0.30f,
                1.0f
            )
    }

    // ============================================================
    // HEDEF SATURATION
    // ============================================================

    private fun calculateTargetSaturation(
        targetSaturation: Float,
        sourceSaturation: Float,
        sourceValue: Float
    ): Float {

        val target =
            targetSaturation.coerceIn(
                0f,
                1f
            )

        val source =
            sourceSaturation.coerceIn(
                0f,
                1f
            )

        val value =
            sourceValue.coerceIn(
                0f,
                1f
            )

        /*
         * Hedef rengin karakterini koruyoruz.
         *
         * Ancak çok açık kumaşlarda saturation biraz düşürülür.
         * Böylece sonuç plastik boya gibi görünmez.
         */

        val lightFabricReduction =
            if (
                value > 0.85f
            ) {
                0.94f
            } else {
                1.0f
            }

        /*
         * Çok doygun hedef renkleri hafifçe kumaş karakterine
         * yaklaştır.
         */
        val fabricInfluence =
            (
                    source * 0.08f
                    )
                .coerceIn(
                    0f,
                    0.08f
                )

        return (
                target *
                        (1f - fabricInfluence)
                        +
                        max(
                            target,
                            source
                        ) *
                        fabricInfluence
                )
            .coerceIn(
                0f,
                1f
            ) *
                lightFabricReduction
    }

    // ============================================================
    // HEDEF VALUE
    // ============================================================

    private fun calculateTargetValue(
        targetValue: Float,
        fabricLightness: Float,
        sourceValue: Float
    ): Float {

        val target =
            targetValue.coerceIn(
                0f,
                1f
            )

        val lightness =
            fabricLightness.coerceIn(
                0.30f,
                1.0f
            )

        val source =
            sourceValue.coerceIn(
                0f,
                1f
            )

        /*
         * Hedef rengin kendi parlaklığı + kumaşın ışık bilgisi.
         */

        val mapped =
            target *
                    (
                            0.62f +
                                    lightness * 0.38f
                            )

        /*
         * Çok koyu kumaşlarda minimum görünürlük.
         *
         * Ancak beyaz kumaşı gereksiz yere karartmıyoruz.
         */

        val minimumValue =
            if (
                source < 0.18f
            ) {
                target *
                        0.52f
            } else {
                target *
                        0.30f
            }

        return max(
            mapped,
            minimumValue
        )
            .coerceIn(
                0.08f,
                1.0f
            )
    }

    // ============================================================
    // KUMAŞ DOKUSU
    // ============================================================

    private fun preserveFabricTexture(
        originalRed: Float,
        originalGreen: Float,
        originalBlue: Float,
        recoloredRed: Float,
        recoloredGreen: Float,
        recoloredBlue: Float
    ): FloatArray {

        val originalLuminance =
            (
                    originalRed * 0.2126f +
                            originalGreen * 0.7152f +
                            originalBlue * 0.0722f
                    ) / 255f

        val recoloredLuminance =
            (
                    recoloredRed * 0.2126f +
                            recoloredGreen * 0.7152f +
                            recoloredBlue * 0.0722f
                    ) / 255f

        /*
         * Orijinal kumaşın ışık farkının küçük bir kısmını
         * geri ekle.
         */
        val textureDifference =
            (
                    originalLuminance -
                            recoloredLuminance
                    )
                .coerceIn(
                    -0.20f,
                    0.20f
                )

        val textureStrength =
            0.18f

        val adjustment =
            textureDifference *
                    textureStrength *
                    255f

        return floatArrayOf(
            (
                    recoloredRed +
                            adjustment
                    )
                .coerceIn(
                    0f,
                    255f
                ),

            (
                    recoloredGreen +
                            adjustment
                    )
                .coerceIn(
                    0f,
                    255f
                ),

            (
                    recoloredBlue +
                            adjustment
                    )
                .coerceIn(
                    0f,
                    255f
                )
        )
    }

    // ============================================================
    // RGB -> HSV
    // ============================================================

    private fun rgbToHsv(
        red: Float,
        green: Float,
        blue: Float
    ): FloatArray {

        val r =
            (red / 255f)
                .coerceIn(
                    0f,
                    1f
                )

        val g =
            (green / 255f)
                .coerceIn(
                    0f,
                    1f
                )

        val b =
            (blue / 255f)
                .coerceIn(
                    0f,
                    1f
                )

        val maxValue =
            max(
                r,
                max(
                    g,
                    b
                )
            )

        val minValue =
            min(
                r,
                min(
                    g,
                    b
                )
            )

        val delta =
            maxValue -
                    minValue

        var hue =
            0f

        if (
            delta != 0f
        ) {

            hue =
                when {

                    maxValue == r -> {
                        60f *
                                (
                                        (g - b) /
                                                delta
                                        )
                    }

                    maxValue == g -> {
                        60f *
                                (
                                        2f +
                                                (b - r) /
                                                delta
                                        )
                    }

                    else -> {
                        60f *
                                (
                                        4f +
                                                (r - g) /
                                                delta
                                        )
                    }
                }

            if (
                hue < 0f
            ) {
                hue += 360f
            }
        }

        val saturation =
            if (
                maxValue == 0f
            ) {
                0f
            } else {
                delta /
                        maxValue
            }

        return floatArrayOf(
            hue,
            saturation,
            maxValue
        )
    }

    // ============================================================
    // HSV -> RGB
    // ============================================================

    private fun hsvToRgb(
        hue: Float,
        saturation: Float,
        value: Float
    ): FloatArray {

        val h =
            (
                    hue %
                            360f +
                            360f
                    ) % 360f

        val s =
            saturation.coerceIn(
                0f,
                1f
            )

        val v =
            value.coerceIn(
                0f,
                1f
            )

        val c =
            v * s

        val x =
            c *
                    (
                            1f -
                                    abs(
                                        (
                                                h / 60f
                                                ) % 2f -
                                                1f
                                    )
                            )

        val m =
            v - c

        val rgb =
            when {

                h < 60f ->
                    floatArrayOf(
                        c,
                        x,
                        0f
                    )

                h < 120f ->
                    floatArrayOf(
                        x,
                        c,
                        0f
                    )

                h < 180f ->
                    floatArrayOf(
                        0f,
                        c,
                        x
                    )

                h < 240f ->
                    floatArrayOf(
                        0f,
                        x,
                        c
                    )

                h < 300f ->
                    floatArrayOf(
                        x,
                        0f,
                        c
                    )

                else ->
                    floatArrayOf(
                        c,
                        0f,
                        x
                    )
            }

        return floatArrayOf(
            (
                    rgb[0] + m
                    ) * 255f,

            (
                    rgb[1] + m
                    ) * 255f,

            (
                    rgb[2] + m
                    ) * 255f
        )
    }

    // ============================================================
    // ARKA PLAN PALETİ
    // ============================================================

    private fun buildBackgroundPalette(
        source: Bitmap
    ): List<Int> {

        val colors =
            mutableListOf<Int>()

        val width =
            source.width

        val height =
            source.height

        if (
            width <= 0 ||
            height <= 0
        ) {
            return emptyList()
        }

        val step =
            maxOf(
                8,
                minOf(
                    width,
                    height
                ) / 24
            )

        var x =
            0

        // --------------------------------------------------------
        // ÜST
        // --------------------------------------------------------

        while (
            x < width
        ) {

            colors.add(
                source.getPixel(
                    x,
                    0
                )
            )

            x += step
        }

        // --------------------------------------------------------
        // ALT
        // --------------------------------------------------------

        x = 0

        while (
            x < width
        ) {

            colors.add(
                source.getPixel(
                    x,
                    height - 1
                )
            )

            x += step
        }

        // --------------------------------------------------------
        // SOL
        // --------------------------------------------------------

        var y =
            0

        while (
            y < height
        ) {

            colors.add(
                source.getPixel(
                    0,
                    y
                )
            )

            y += step
        }

        // --------------------------------------------------------
        // SAĞ
        // --------------------------------------------------------

        y = 0

        while (
            y < height
        ) {

            colors.add(
                source.getPixel(
                    width - 1,
                    y
                )
            )

            y += step
        }

        return colors
    }

    // ============================================================
    // GÜÇLÜ ARKA PLAN EŞLEŞMESİ
    // ============================================================

    /**
     * Eski sistemdeki 28f toleranslı kontrol çok agresifti.
     *
     * Yeni sistem yalnızca gerçekten çok yakın pikselleri
     * korur.
     */
    private fun isStrongBackgroundMatch(
        red: Float,
        green: Float,
        blue: Float,
        backgroundPalette: List<Int>
    ): Boolean {

        if (
            backgroundPalette.isEmpty()
        ) {
            return false
        }

        for (
        backgroundColor in
        backgroundPalette
        ) {

            val backgroundRed =
                Color.red(
                    backgroundColor
                ).toFloat()

            val backgroundGreen =
                Color.green(
                    backgroundColor
                ).toFloat()

            val backgroundBlue =
                Color.blue(
                    backgroundColor
                ).toFloat()

            val distance =
                colorDistance(
                    red,
                    green,
                    blue,
                    backgroundRed,
                    backgroundGreen,
                    backgroundBlue
                )

            if (
                distance <= 10f
            ) {
                return true
            }
        }

        return false
    }

    // ============================================================
    // ÇOK AÇIK ARKA PLAN
    // ============================================================

    private fun isVeryLightBackground(
        red: Float,
        green: Float,
        blue: Float
    ): Boolean {

        /*
         * Neredeyse saf beyaz.
         *
         * 245 yerine 250 kullanılıyor.
         *
         * Böylece açık renk kıyafetler yanlışlıkla
         * engellenmez.
         */
        return red >= 250f &&
                green >= 250f &&
                blue >= 250f
    }

    // ============================================================
    // RENK MESAFESİ
    // ============================================================

    private fun colorDistance(
        red1: Float,
        green1: Float,
        blue1: Float,
        red2: Float,
        green2: Float,
        blue2: Float
    ): Float {

        val redDifference =
            red1 -
                    red2

        val greenDifference =
            green1 -
                    green2

        val blueDifference =
            blue1 -
                    blue2

        return sqrt(
            redDifference *
                    redDifference +
                    greenDifference *
                    greenDifference +
                    blueDifference *
                    blueDifference
        )
    }
}