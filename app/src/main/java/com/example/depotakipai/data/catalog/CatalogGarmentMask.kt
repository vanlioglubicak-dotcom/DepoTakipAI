package com.example.depotakipai.data.catalog

import android.graphics.Bitmap
import android.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.FloatBuffer
import kotlin.coroutines.resume
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * ============================================================
 * KATALOG AI KIYAFET MASKESİ
 * ============================================================
 *
 * AMAÇ:
 *
 * Katalog ürün görselinde yalnızca kıyafetin gerçek kumaş
 * bölgesini recolor motoruna vermek.
 *
 * HEDEF:
 *
 *   Yüz       -> KORU
 *   Saç       -> KORU
 *   Ten       -> KORU
 *   El        -> KORU
 *   Bacak     -> KORU
 *   Ayakkabı  -> KORU
 *   Arka plan -> KORU
 *   Zemin     -> KORU
 *   Kıyafet   -> RENKLENDİR
 *
 * ÖNEMLİ:
 *
 * ML Kit Subject Segmentation insan/ön plan segmentasyonu
 * sağlar. Tek başına kıyafet segmentasyonu değildir.
 *
 * Bu sınıf:
 *
 * 1. ML Kit AI foreground confidence
 * 2. Ten rengi dışlama
 * 3. Kafa dışlama
 * 4. Ayakkabı / alt bölge güvenlik sınırı
 * 5. Merkezi kıyafet seed alanı
 * 6. Bağlantılı bileşen analizi
 * 7. Arka plan kontrolü
 * 8. Kenar yumuşatma
 *
 * katmanlarını birleştirir.
 *
 * Böylece önceki sistemde görülen:
 *
 * - pembe zemin
 * - ayakkabı
 * - bacak
 * - yüz
 * - saç
 *
 * gibi alanların recolor edilmesi ciddi şekilde azaltılır.
 */
object CatalogGarmentMask {

    // ============================================================
    // SONUÇ
    // ============================================================

    data class MaskResult(
        val mask: Bitmap,
        val coverage: Float
    )

    // ============================================================
    // AI KIYAFET MASKESİ
    // ============================================================

    /**
     * Gerçek AI ön plan maskesini alır ve bunu kıyafet odaklı
     * ikinci aşama filtrelerden geçirir.
     *
     * suspend olmasının nedeni ML Kit işleminin asenkron olmasıdır.
     */
    suspend fun createAiMask(
        source: Bitmap,
        backgroundTolerance: Int = 24
    ): MaskResult? {

        if (
            source.width < 2 ||
            source.height < 2
        ) {
            return null
        }

        /*
         * ML Kit doğruluğu için mümkünse 512x512 ve üzeri
         * görseller kullanılmalıdır.
         */
        if (
            source.width < 512 ||
            source.height < 512
        ) {
            return null
        }

        return try {

            // ----------------------------------------------------
            // 1. ML KIT SUBJECT SEGMENTATION
            // ----------------------------------------------------

            val options =
                SubjectSegmenterOptions.Builder()
                    .enableForegroundConfidenceMask()
                    .build()

            val segmenter =
                SubjectSegmentation.getClient(
                    options
                )

            val inputImage =
                InputImage.fromBitmap(
                    source,
                    0
                )

            val result =
                awaitSegmentation(
                    segmenter = segmenter,
                    image = inputImage
                )

            val foregroundConfidence =
                result.foregroundConfidenceMask
                    ?: return null

            val confidence =
                foregroundConfidence.duplicate()

            confidence.rewind()

            val width =
                source.width

            val height =
                source.height

            val totalPixels =
                width.toLong() * height.toLong()

            if (
                confidence.capacity() <
                totalPixels.toInt()
            ) {
                return null
            }

            // ----------------------------------------------------
            // 2. ARKA PLAN PALETİ
            // ----------------------------------------------------

            val backgroundPalette =
                buildBackgroundPalette(
                    source = source
                )

            // ----------------------------------------------------
            // 3. AI ADAY MASKESİ
            // ----------------------------------------------------

            val candidate =
                Array(
                    height
                ) {
                    BooleanArray(
                        width
                    )
                }

            val candidateConfidence =
                Array(
                    height
                ) {
                    FloatArray(
                        width
                    )
                }

            for (y in 0 until height) {

                for (x in 0 until width) {

                    val index =
                        y * width + x

                    val aiConfidence =
                        confidence.get(
                            index
                        )

                    candidateConfidence[y][x] =
                        aiConfidence

                    if (
                        aiConfidence <
                        AI_FOREGROUND_THRESHOLD
                    ) {
                        continue
                    }

                    val pixel =
                        source.getPixel(
                            x,
                            y
                        )

                    val alpha =
                        Color.alpha(
                            pixel
                        )

                    if (
                        alpha < 20
                    ) {
                        continue
                    }

                    val red =
                        Color.red(pixel)

                    val green =
                        Color.green(pixel)

                    val blue =
                        Color.blue(pixel)

                    // ------------------------------------------------
                    // TEN KORUMA
                    // ------------------------------------------------

                    if (
                        isSkinTone(
                            red,
                            green,
                            blue
                        )
                    ) {
                        continue
                    }

                    if (
                        isLightSkin(
                            red,
                            green,
                            blue
                        )
                    ) {
                        continue
                    }

                    // ------------------------------------------------
                    // KAFA / SAÇ KORUMA
                    // ------------------------------------------------

                    if (
                        isHeadArea(
                            x = x,
                            y = y,
                            width = width,
                            height = height
                        )
                    ) {
                        continue
                    }

                    // ------------------------------------------------
                    // GÖRSEL KENAR KORUMASI
                    // ------------------------------------------------

                    if (
                        isExtremeSide(
                            x = x,
                            width = width
                        )
                    ) {
                        continue
                    }

                    // ------------------------------------------------
                    // ALT AYAK / ZEMİN KORUMASI
                    // ------------------------------------------------
                    //
                    // AI ön plan maskesi zemini yanlışlıkla insanın
                    // parçası olarak görürse aşağıdaki güvenlik
                    // katmanı devreye girer.
                    //
                    // Ancak elbisenin alt kısmını tamamen kesmemek
                    // için burada yalnızca çok düşük confidence
                    // değerlerini reddediyoruz.

                    if (
                        isVeryBottomArea(
                            y = y,
                            height = height
                        ) &&
                        aiConfidence <
                        HIGH_CONFIDENCE
                    ) {
                        continue
                    }

                    // ------------------------------------------------
                    // ARKA PLAN KORUMA
                    // ------------------------------------------------
                    //
                    // Yüksek AI confidence varsa beyaz kıyafeti
                    // yanlışlıkla çıkarmıyoruz.
                    //
                    // Düşük/orta confidence alanlarında ise arka
                    // plan benzerliği daha agresif uygulanır.

                    if (
                        aiConfidence <
                        HIGH_CONFIDENCE &&
                        isBackgroundLike(
                            red = red.toFloat(),
                            green = green.toFloat(),
                            blue = blue.toFloat(),
                            backgroundPalette = backgroundPalette,
                            tolerance = backgroundTolerance
                        )
                    ) {
                        continue
                    }

                    candidate[y][x] =
                        true
                }
            }

            // ----------------------------------------------------
            // 4. MERKEZ KIYAFET SEED ALANI
            // ----------------------------------------------------
            //
            // Burada insanın gövde/elbise bölgesini bulmaya
            // çalışıyoruz.
            //
            // Amaç:
            //
            // pembe zemin gibi ayrı bir foreground parçasını
            // kıyafet sanmamak.

            val seed =
                Array(
                    height
                ) {
                    BooleanArray(
                        width
                    )
                }

            var seedCount =
                0

            val seedTop =
                (height * 0.27f)
                    .toInt()

            val seedBottom =
                (height * 0.84f)
                    .toInt()

            val seedLeft =
                (width * 0.20f)
                    .toInt()

            val seedRight =
                (width * 0.80f)
                    .toInt()

            for (
            y in seedTop until
                    seedBottom.coerceAtMost(height)
            ) {

                for (
                x in seedLeft until
                        seedRight.coerceAtMost(width)
                ) {

                    if (
                        !candidate[y][x]
                    ) {
                        continue
                    }

                    val aiConfidence =
                        candidateConfidence[y][x]

                    if (
                        aiConfidence <
                        SEED_CONFIDENCE
                    ) {
                        continue
                    }

                    seed[y][x] =
                        true

                    seedCount++
                }
            }

            if (
                seedCount < MIN_SEED_PIXELS
            ) {
                return null
            }

            // ----------------------------------------------------
            // 5. BAĞLANTILI BİLEŞEN
            // ----------------------------------------------------
            //
            // AI foreground içinde birden fazla parça olabilir:
            //
            // insan
            // ayakkabı
            // zemin gölgesi
            // çanta
            // aksesuar
            //
            // Biz merkezi kıyafet seed alanına bağlı olan
            // bileşeni seçiyoruz.

            val connected =
                connectedComponentFromSeeds(
                    candidate = candidate,
                    seed = seed
                )

            if (
                connected.count < MIN_GARMENT_PIXELS
            ) {
                return null
            }

            // ----------------------------------------------------
            // 6. KENAR TEMİZLEME
            // ----------------------------------------------------

            val mask =
                Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.ALPHA_8
                )

            var selectedPixels =
                0

            for (y in 0 until height) {

                for (x in 0 until width) {

                    if (
                        !connected.pixels[y][x]
                    ) {
                        mask.setPixel(
                            x,
                            y,
                            Color.argb(
                                0,
                                255,
                                255,
                                255
                            )
                        )

                        continue
                    }

                    val aiConfidence =
                        candidateConfidence[y][x]

                    /*
                     * AI confidence'a göre alpha.
                     *
                     * Yüksek confidence:
                     * tamamen kıyafet.
                     *
                     * Orta confidence:
                     * yumuşak geçiş.
                     */
                    val maskAlpha =
                        when {

                            aiConfidence >=
                                    0.85f -> 255

                            aiConfidence >=
                                    0.70f -> 220

                            aiConfidence >=
                                    0.60f -> 175

                            else -> 120
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

                    if (
                        maskAlpha > 0
                    ) {
                        selectedPixels++
                    }
                }
            }

            val coverage =
                if (
                    totalPixels > 0L
                ) {
                    selectedPixels.toFloat() /
                            totalPixels.toFloat()
                } else {
                    0f
                }

            if (
                coverage <
                MIN_COVERAGE
            ) {
                return null
            }

            if (
                coverage >
                MAX_COVERAGE
            ) {
                return null
            }

            // ----------------------------------------------------
            // 7. ÇOK HAFİF KENAR YUMUŞATMA
            // ----------------------------------------------------
            //
            // Daha önceki sistemde radius=2 fazla agresifti.
            //
            // Pembe zemin gibi alanlara taşmaması için radius=1.

            val softened =
                soften(
                    mask = mask,
                    radius = 1
                )

            MaskResult(
                mask = softened,
                coverage = coverage
            )

        } catch (
            _: Exception
        ) {
            null
        }
    }

    // ============================================================
    // ML KIT ASYNC -> SUSPEND
    // ============================================================

    private suspend fun awaitSegmentation(
        segmenter: com.google.mlkit.vision.segmentation.subject.SubjectSegmenter,
        image: InputImage
    ): com.google.mlkit.vision.segmentation.subject.SubjectSegmentationResult =
        suspendCancellableCoroutine { continuation ->

            segmenter
                .process(image)
                .addOnSuccessListener { result ->

                    if (
                        continuation.isActive
                    ) {
                        continuation.resume(
                            result
                        )
                    }
                }
                .addOnFailureListener {

                    if (
                        continuation.isActive
                    ) {
                        continuation.resumeWith(
                            Result.failure(
                                it
                            )
                        )
                    }
                }
        }

    // ============================================================
    // BAĞLANTILI BİLEŞEN
    // ============================================================

    private data class ConnectedComponent(
        val pixels: Array<BooleanArray>,
        val count: Int
    )

    /**
     * Merkezi kıyafet seed alanından bağlantılı olan
     * foreground alanını bulur.
     *
     * Böylece görüntünün başka bir yerindeki:
     *
     * - zemin
     * - gölge
     * - ayakkabı
     * - kopuk aksesuar
     *
     * kıyafet maskesine dahil edilmez.
     */
    private fun connectedComponentFromSeeds(
        candidate: Array<BooleanArray>,
        seed: Array<BooleanArray>
    ): ConnectedComponent {

        val height =
            candidate.size

        if (
            height == 0
        ) {
            return ConnectedComponent(
                pixels = emptyArray(),
                count = 0
            )
        }

        val width =
            candidate[0].size

        val visited =
            Array(
                height
            ) {
                BooleanArray(
                    width
                )
            }

        val result =
            Array(
                height
            ) {
                BooleanArray(
                    width
                )
            }

        val queueX =
            IntArray(
                width * height
            )

        val queueY =
            IntArray(
                width * height
            )

        var head =
            0

        var tail =
            0

        // --------------------------------------------------------
        // SEED EKLE
        // --------------------------------------------------------

        for (y in 0 until height) {

            for (x in 0 until width) {

                if (
                    seed[y][x] &&
                    candidate[y][x] &&
                    !visited[y][x]
                ) {

                    visited[y][x] =
                        true

                    queueX[tail] =
                        x

                    queueY[tail] =
                        y

                    tail++
                }
            }
        }

        // --------------------------------------------------------
        // FLOOD FILL
        // --------------------------------------------------------

        var count =
            0

        val directions =
            arrayOf(
                intArrayOf(-1, -1),
                intArrayOf(0, -1),
                intArrayOf(1, -1),
                intArrayOf(-1, 0),
                intArrayOf(1, 0),
                intArrayOf(-1, 1),
                intArrayOf(0, 1),
                intArrayOf(1, 1)
            )

        while (
            head < tail
        ) {

            val x =
                queueX[head]

            val y =
                queueY[head]

            head++

            result[y][x] =
                true

            count++

            for (
            direction in directions
            ) {

                val nx =
                    x + direction[0]

                val ny =
                    y + direction[1]

                if (
                    nx < 0 ||
                    nx >= width ||
                    ny < 0 ||
                    ny >= height
                ) {
                    continue
                }

                if (
                    visited[ny][nx]
                ) {
                    continue
                }

                if (
                    !candidate[ny][nx]
                ) {
                    continue
                }

                visited[ny][nx] =
                    true

                queueX[tail] =
                    nx

                queueY[tail] =
                    ny

                tail++
            }
        }

        return ConnectedComponent(
            pixels = result,
            count = count
        )
    }

    // ============================================================
    // TEN RENGİ
    // ============================================================

    private fun isSkinTone(
        red: Int,
        green: Int,
        blue: Int
    ): Boolean {

        val maxChannel =
            max(
                red,
                max(
                    green,
                    blue
                )
            )

        val minChannel =
            min(
                red,
                min(
                    green,
                    blue
                )
            )

        val difference =
            maxChannel - minChannel

        return red > 80 &&
                green > 35 &&
                blue > 20 &&
                red >= green + 12 &&
                red >= blue + 20 &&
                difference >= 25 &&
                red.toFloat() /
                max(
                    green.toFloat(),
                    1f
                ) >= 1.05f
    }

    private fun isLightSkin(
        red: Int,
        green: Int,
        blue: Int
    ): Boolean {

        return red >= 150 &&
                green >= 95 &&
                blue >= 65 &&
                red >= green &&
                green >= blue &&
                red - blue >= 35
    }

    // ============================================================
    // KAFA ALANI
    // ============================================================

    private fun isHeadArea(
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ): Boolean {

        val headBottom =
            height * 0.28f

        val headLeft =
            width * 0.20f

        val headRight =
            width * 0.80f

        return y < headBottom &&
                x > headLeft &&
                x < headRight
    }

    // ============================================================
    // KENAR
    // ============================================================

    private fun isExtremeSide(
        x: Int,
        width: Int
    ): Boolean {

        return x <
                width * 0.015f ||
                x >
                width * 0.985f
    }

    // ============================================================
    // ALT BÖLGE
    // ============================================================

    private fun isVeryBottomArea(
        y: Int,
        height: Int
    ): Boolean {

        return y >
                height * 0.93f
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

        // ÜST
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

        // ALT
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

        var y =
            0

        // SOL
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

        // SAĞ
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
    // ARKA PLAN BENZERLİĞİ
    // ============================================================

    private fun isBackgroundLike(
        red: Float,
        green: Float,
        blue: Float,
        backgroundPalette: List<Int>,
        tolerance: Int
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
                distance <=
                tolerance.toFloat()
            ) {
                return true
            }
        }

        return false
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

        val dr =
            red1 - red2

        val dg =
            green1 - green2

        val db =
            blue1 - blue2

        return kotlin.math.sqrt(
            dr * dr +
                    dg * dg +
                    db * db
        )
    }

    // ============================================================
    // ESKİ SİSTEM İÇİN GÜVENLİ FALLBACK
    // ============================================================

    /**
     * AI kullanılamadığı durumda eski maskeyi üretir.
     *
     * Bu fonksiyon API uyumluluğu için korunmaktadır.
     */
    fun createMask(
        source: Bitmap,
        backgroundTolerance: Int = 18
    ): MaskResult {

        val width =
            source.width

        val height =
            source.height

        val mask =
            Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ALPHA_8
            )

        if (
            width <= 0 ||
            height <= 0
        ) {
            return MaskResult(
                mask = mask,
                coverage = 0f
            )
        }

        val headEnd =
            (height * 0.28f)
                .toInt()

        val centerLeft =
            (width * 0.18f)
                .toInt()

        val centerRight =
            (width * 0.82f)
                .toInt()

        var selectedPixels =
            0

        val totalPixels =
            width.toLong() *
                    height.toLong()

        for (y in 0 until height) {

            for (x in 0 until width) {

                val pixel =
                    source.getPixel(
                        x,
                        y
                    )

                val alpha =
                    Color.alpha(
                        pixel
                    )

                val red =
                    Color.red(
                        pixel
                    )

                val green =
                    Color.green(
                        pixel
                    )

                val blue =
                    Color.blue(
                        pixel
                    )

                val isTransparent =
                    alpha < 20

                val isNearWhite =
                    red >=
                            255 -
                            backgroundTolerance &&
                            green >=
                            255 -
                            backgroundTolerance &&
                            blue >=
                            255 -
                            backgroundTolerance

                val isNearGrayBackground =
                    abs(
                        red - green
                    ) <= 6 &&
                            abs(
                                green - blue
                            ) <= 6 &&
                            red >= 215

                val isSkin =
                    isSkinTone(
                        red,
                        green,
                        blue
                    ) ||
                            isLightSkin(
                                red,
                                green,
                                blue
                            )

                val isHead =
                    y < headEnd &&
                            x >
                            width * 0.20f &&
                            x <
                            width * 0.80f

                val isExtreme =
                    isExtremeSide(
                        x,
                        width
                    )

                if (
                    isTransparent ||
                    isNearWhite ||
                    isNearGrayBackground ||
                    isSkin ||
                    isHead ||
                    isExtreme
                ) {

                    mask.setPixel(
                        x,
                        y,
                        Color.argb(
                            0,
                            255,
                            255,
                            255
                        )
                    )

                    continue
                }

                val alphaValue =
                    if (
                        x >= centerLeft &&
                        x <= centerRight
                    ) {
                        255
                    } else {
                        60
                    }

                selectedPixels++

                mask.setPixel(
                    x,
                    y,
                    Color.argb(
                        alphaValue,
                        255,
                        255,
                        255
                    )
                )
            }
        }

        val coverage =
            if (
                totalPixels > 0L
            ) {
                selectedPixels.toFloat() /
                        totalPixels.toFloat()
            } else {
                0f
            }

        return MaskResult(
            mask = mask,
            coverage = coverage
        )
    }

    // ============================================================
    // MASKE KULLANILABİLİR Mİ?
    // ============================================================

    fun isUsable(
        result: MaskResult
    ): Boolean {

        return result.coverage >=
                0.008f &&
                result.coverage <=
                0.60f
    }

    // ============================================================
    // KENAR YUMUŞATMA
    // ============================================================

    fun soften(
        mask: Bitmap,
        radius: Int = 1
    ): Bitmap {

        if (
            radius <= 0
        ) {
            return mask.copy(
                Bitmap.Config.ALPHA_8,
                false
            )
        }

        val width =
            mask.width

        val height =
            mask.height

        val result =
            Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ALPHA_8
            )

        for (y in 0 until height) {

            for (x in 0 until width) {

                var total =
                    0

                var count =
                    0

                for (
                dy in -radius..radius
                ) {

                    for (
                    dx in -radius..radius
                    ) {

                        val nx =
                            x + dx

                        val ny =
                            y + dy

                        if (
                            nx >= 0 &&
                            nx < width &&
                            ny >= 0 &&
                            ny < height
                        ) {

                            total +=
                                Color.alpha(
                                    mask.getPixel(
                                        nx,
                                        ny
                                    )
                                )

                            count++
                        }
                    }
                }

                val average =
                    if (
                        count > 0
                    ) {
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

    // ============================================================
    // SABİTLER
    // ============================================================

    private const val
            AI_FOREGROUND_THRESHOLD =
        0.55f

    private const val
            SEED_CONFIDENCE =
        0.72f

    private const val
            HIGH_CONFIDENCE =
        0.82f

    private const val
            MIN_SEED_PIXELS =
        120

    private const val
            MIN_GARMENT_PIXELS =
        400

    private const val
            MIN_COVERAGE =
        0.008f

    private const val
            MAX_COVERAGE =
        0.60f
}