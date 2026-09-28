package com.example.depotakipai.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.delay

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF808080)
private val SuccessGreen = Color(0xFF1B8F3A)

@Composable
fun CameraScanScreen(
    onBack: () -> Unit = {},
    onScanConfirmed: (List<CameraScanResult>) -> Unit = {},
    mode: CameraScanMode = CameraScanMode.PRODUCT
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    /*
     * =========================================================
     * KAMERA İZNİ
     * =========================================================
     */

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /*
     * =========================================================
     * OKUNAN ÜRÜNLER
     * =========================================================
     */

    val scannedResults =
        remember {
            mutableStateListOf<CameraScanResult>()
        }

    var lastScannedCode by remember {
        mutableStateOf("")
    }

    val lastScanTime =
        remember {
            AtomicLong(0L)
        }

    /*
     * Barkod okundu görsel durumu
     */

    var barcodeReadSuccess by remember {
        mutableStateOf(false)
    }

    /*
     * Son okunan ürün
     */

    var lastReadText by remember {
        mutableStateOf("")
    }

    /*
     * Kamera analiz thread'i
     */

    val cameraExecutor =
        remember {
            Executors.newSingleThreadExecutor()
        }

    /*
     * UI thread
     */

    val mainHandler =
        remember {
            Handler(Looper.getMainLooper())
        }

    /*
     * =========================================================
     * TEMİZLEME
     * =========================================================
     */

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            mainHandler.removeCallbacksAndMessages(null)
        }
    }

    /*
     * =========================================================
     * SAYILAR
     * =========================================================
     */

    val totalScanned = scannedResults.size

    val differentProducts =
        scannedResults
            .mapNotNull { item ->
                item.systemBarcode
                    ?.takeIf { it.isNotBlank() }
                    ?: item.productCode
                        ?.takeIf { it.isNotBlank() }
            }
            .distinct()
            .size

    /*
     * =========================================================
     * OKUNDU DURUMUNU KISA SÜRE GÖSTER
     * =========================================================
     */

    LaunchedEffect(barcodeReadSuccess) {

        if (barcodeReadSuccess) {

            delay(900)

            barcodeReadSuccess = false
        }
    }

    /*
     * =========================================================
     * ANA EKRAN
     * =========================================================
     */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        /*
         * =====================================================
         * CANLI KAMERA
         * =====================================================
         */

        if (hasCameraPermission) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),

                factory = { viewContext ->

                    val previewView =
                        PreviewView(viewContext)

                    val cameraProviderFuture =
                        ProcessCameraProvider.getInstance(
                            viewContext
                        )

                    cameraProviderFuture.addListener({

                        try {

                            val provider =
                                cameraProviderFuture.get()

                            /*
                             * PREVIEW
                             */

                            val preview =
                                Preview.Builder()
                                    .build()
                                    .also { previewUseCase ->

                                        previewUseCase.surfaceProvider =
                                            previewView.surfaceProvider
                                    }

                            /*
                             * ANALİZ
                             */

                            val imageAnalysis =
                                ImageAnalysis.Builder()
                                    .setBackpressureStrategy(
                                        ImageAnalysis
                                            .STRATEGY_KEEP_ONLY_LATEST
                                    )
                                    .build()

                            /*
                             * MEVCUT CAMERA ANALYZER
                             */

                            val analyzer =
                                CameraAnalyzer { result ->

                                    val now =
                                        System.currentTimeMillis()

                                    val currentCode =
                                        result.systemBarcode
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: result.productCode
                                                ?.takeIf {
                                                    it.isNotBlank()
                                                }
                                            ?: ""

                                    /*
                                     * Geçersiz sonuç
                                     */

                                    if (currentCode.isBlank()) {
                                        return@CameraAnalyzer
                                    }

                                    /*
                                     * Aynı barkodun aynı anda
                                     * tekrar tekrar okunmasını engelle.
                                     */

                                    if (
                                        currentCode ==
                                        lastScannedCode &&
                                        now -
                                        lastScanTime.get() <
                                        1200L
                                    ) {
                                        return@CameraAnalyzer
                                    }

                                    lastScannedCode =
                                        currentCode

                                    lastScanTime.set(now)

                                    /*
                                     * OKUMA SESİ
                                     */

                                    playBarcodeSound(context)

                                    /*
                                     * TİTREŞİM
                                     */

                                    vibrateOnScan(context)

                                    /*
                                     * UI THREAD'E GEÇ
                                     */

                                    mainHandler.post {

                                        scannedResults.add(result)

                                        lastReadText =
                                            currentCode

                                        barcodeReadSuccess = true
                                    }
                                }

                            imageAnalysis.setAnalyzer(
                                cameraExecutor,
                                analyzer
                            )

                            /*
                             * ARKA KAMERA
                             */

                            val cameraSelector =
                                CameraSelector.DEFAULT_BACK_CAMERA

                            provider.unbindAll()

                            provider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )

                        } catch (_: Exception) {
                            // Kamera başlatma hatası
                        }

                    }, ContextCompat.getMainExecutor(viewContext))

                    previewView
                }
            )
        }

        /*
         * =====================================================
         * TRENDYOL TARZI KAMERA OVERLAY
         * =====================================================
         */

        if (hasCameraPermission) {

            BarcodeScannerOverlay(
                success = barcodeReadSuccess
            )
        }

        /*
         * =====================================================
         * ÜST BİLGİLER
         * =====================================================
         */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            ScanInfoButton(
                modifier = Modifier.weight(1f),
                title = "OKUNAN",
                value = "$totalScanned",
                color = DarkRed
            )

            ScanInfoButton(
                modifier = Modifier.weight(1f),
                title = "FARKLI ÜRÜN",
                value = "$differentProducts",
                color = SteelBlue
            )
        }

        /*
         * =====================================================
         * SON OKUNAN BİLGİSİ
         * =====================================================
         *
         * Sürekli yazı yok.
         * Sadece gerçekten barkod okunduğunda kısa süre görünür.
         */

        if (barcodeReadSuccess) {

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 340.dp)
                    .background(
                        color = SuccessGreen,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✓ OKUNDU",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        /*
         * =====================================================
         * GERİ
         * =====================================================
         */

        Button(
            onClick = onBack,

            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(
                    end = 16.dp,
                    top = 72.dp
                ),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        Color.Black.copy(alpha = 0.55f)
                ),

            shape =
                RoundedCornerShape(12.dp)
        ) {

            Text(
                text = "Geri",
                color = Color.White
            )
        }

        /*
         * =====================================================
         * ALT ONAY / RED BUTONLARI
         * =====================================================
         *
         * Global alt menünün üzerine binmemesi için
         * yukarı taşındı.
         */

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    bottom = 108.dp
                ),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            /*
             * REDDET
             */

            Button(
                onClick = {

                    scannedResults.clear()

                    lastScannedCode = ""

                    lastScanTime.set(0L)

                    barcodeReadSuccess = false

                    lastReadText = ""
                },

                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Gray
                    ),

                shape =
                    RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "✕  REDDET",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            /*
             * ONAYLA
             */

            Button(
                onClick = {

                    if (scannedResults.isNotEmpty()) {

                        onScanConfirmed(
                            scannedResults.toList()
                        )
                    }
                },

                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = DarkRed
                    ),

                shape =
                    RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "✓  ONAYLA",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


/*
 * =============================================================
 * OKUNAN / FARKLI ÜRÜN BUTONU
 * =============================================================
 */

@Composable
private fun ScanInfoButton(
    modifier: Modifier,
    title: String,
    value: String,
    color: Color
) {

    Box(
        modifier = modifier
            .height(62.dp)
            .background(
                color = Color.Black.copy(alpha = 0.55f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { },

        contentAlignment = Alignment.Center
    ) {

        Row(
            horizontalArrangement =
                Arrangement.Center,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.size(7.dp)
            )

            Text(
                text = value,
                color = color,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


/*
 * =============================================================
 * TRENDYOL TARZI BARKOD OKUMA ALANI
 * =============================================================
 */

@Composable
private fun BarcodeScannerOverlay(
    success: Boolean
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        /*
         * =====================================================
         * DIŞ ALANI KARART
         * =====================================================
         */

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy =
                        CompositingStrategy.Offscreen
                }
        ) {

            drawRect(
                color =
                    Color.Black.copy(alpha = 0.50f)
            )

            /*
             * KARE OKUMA ALANI
             */

            val scanSize =
                minOf(
                    size.width * 0.76f,
                    size.height * 0.34f
                )

            val left =
                (size.width - scanSize) / 2f

            val top =
                (size.height - scanSize) / 2f

            drawRect(
                color = Color.Transparent,

                topLeft =
                    androidx.compose.ui.geometry.Offset(
                        x = left,
                        y = top
                    ),

                size =
                    androidx.compose.ui.geometry.Size(
                        width = scanSize,
                        height = scanSize
                    ),

                blendMode = BlendMode.Clear
            )
        }

        /*
         * =====================================================
         * KARE ÇERÇEVE
         * =====================================================
         */

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(280.dp)
        ) {

            ScannerSquare(
                success = success
            )
        }
    }
}


/*
 * =============================================================
 * KARE ÇERÇEVE + LAZER
 * =============================================================
 */

@Composable
private fun ScannerSquare(
    success: Boolean
) {

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        val corner =
            42.dp.toPx()

        val stroke =
            4.dp.toPx()

        val frameColor =
            if (success) {
                SuccessGreen
            } else {
                Color.White
            }

        /*
         * =====================================================
         * SOL ÜST
         * =====================================================
         */

        drawLine(
            color = frameColor,
            start =
                androidx.compose.ui.geometry.Offset(
                    0f,
                    corner
                ),
            end =
                androidx.compose.ui.geometry.Offset(
                    0f,
                    0f
                ),
            strokeWidth = stroke
        )

        drawLine(
            color = frameColor,
            start =
                androidx.compose.ui.geometry.Offset(
                    0f,
                    0f
                ),
            end =
                androidx.compose.ui.geometry.Offset(
                    corner,
                    0f
                ),
            strokeWidth = stroke
        )

        /*
         * =====================================================
         * SAĞ ÜST
         * =====================================================
         */

        drawLine(
            color = frameColor,
            start =
                androidx.compose.ui.geometry.Offset(
                    size.width - corner,
                    0f
                ),
            end =
                androidx.compose.ui.geometry.Offset(
                    size.width,
                    0f
                ),
            strokeWidth = stroke
        )

        drawLine(
            color = frameColor,
            start =
                androidx.compose.ui.geometry.Offset(
                    size.width,
                    0f
                ),
            end =
                androidx.compose.ui.geometry.Offset(
                    size.width,
                    corner
                ),
            strokeWidth = stroke
        )

        /*
         * =====================================================
         * SOL ALT
         * =====================================================
         */

        drawLine(
            color = frameColor,
            start =
                androidx.compose.ui.geometry.Offset(
                    0f,
                    size.height - corner
                ),
            end =
                androidx.compose.ui.geometry.Offset(
                    0f,
                    size.height
                ),
            strokeWidth = stroke
        )

        drawLine(
            color = frameColor,
            start =
                androidx.compose.ui.geometry.Offset(
                    0f,
                    size.height
                ),
            end =
                androidx.compose.ui.geometry.Offset(
                    corner,
                    size.height
                ),
            strokeWidth = stroke
        )

        /*
         * =====================================================
         * SAĞ ALT
         * =====================================================
         */

        drawLine(
            color = frameColor,
            start =
                androidx.compose.ui.geometry.Offset(
                    size.width - corner,
                    size.height
                ),
            end =
                androidx.compose.ui.geometry.Offset(
                    size.width,
                    size.height
                ),
            strokeWidth = stroke
        )

        drawLine(
            color = frameColor,
            start =
                androidx.compose.ui.geometry.Offset(
                    size.width,
                    size.height - corner
                ),
            end =
                androidx.compose.ui.geometry.Offset(
                    size.width,
                    size.height
                ),
            strokeWidth = stroke
        )

        /*
         * =====================================================
         * KIRMIZI LAZER ÇİZGİSİ
         * =====================================================
         */

        if (!success) {

            drawLine(
                color = DarkRed.copy(alpha = 0.95f),

                start =
                    androidx.compose.ui.geometry.Offset(
                        18.dp.toPx(),
                        size.height / 2f
                    ),

                end =
                    androidx.compose.ui.geometry.Offset(
                        size.width - 18.dp.toPx(),
                        size.height / 2f
                    ),

                strokeWidth = 3.dp.toPx()
            )
        }
    }
}


/*
 * =============================================================
 * BARKOD OKUMA SESİ
 * =============================================================
 */

private fun playBarcodeSound(
    context: Context
) {

    try {

        val toneGenerator =
            ToneGenerator(
                AudioManager.STREAM_NOTIFICATION,
                100
            )

        toneGenerator.startTone(
            ToneGenerator.TONE_PROP_BEEP,
            180
        )

        Handler(
            Looper.getMainLooper()
        ).postDelayed({

            toneGenerator.release()

        }, 300)

    } catch (_: Exception) {
    }
}


/*
 * =============================================================
 * BARKOD OKUMA TİTREŞİMİ
 * =============================================================
 */

private fun vibrateOnScan(
    context: Context
) {

    try {

        val vibrator =
            context.getSystemService(
                Context.VIBRATOR_SERVICE
            ) as? Vibrator

        vibrator ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    70L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )

        } else {

            @Suppress("DEPRECATION")
            vibrator.vibrate(70L)
        }

    } catch (_: Exception) {
    }
}