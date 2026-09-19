package com.example.depotakipai.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

import androidx.core.content.ContextCompat

import kotlinx.coroutines.delay

import java.util.concurrent.Executors

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF808080)

@Composable
fun CameraScanScreen(
    onBack: () -> Unit = {},
    onScanResult: (CameraScanResult) -> Unit = {}
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

    /*
     * =========================================================
     * OKUMA SONUCU
     * =========================================================
     */

    var scanResult by remember {
        mutableStateOf<CameraScanResult?>(null)
    }

    /*
     * =========================================================
     * DONDURULMUŞ KAMERA GÖRÜNTÜSÜ
     * =========================================================
     */

    var capturedBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    /*
     * =========================================================
     * DONDURULDU MU?
     * =========================================================
     */

    var isFrozen by remember {
        mutableStateOf(false)
    }

    /*
     * =========================================================
     * 5 SANİYELİK GERİ SAYIM
     * =========================================================
     */

    var countdown by remember {
        mutableStateOf(0)
    }

    /*
     * =========================================================
     * HATA
     * =========================================================
     */

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * =========================================================
     * PREVIEW REFERANSI
     * =========================================================
     */

    var previewViewReference by remember {
        mutableStateOf<PreviewView?>(null)
    }

    /*
     * =========================================================
     * CAMERA PROVIDER
     * =========================================================
     */

    var cameraProviderReference by remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    /*
     * =========================================================
     * CAMERA ANALYSIS
     * =========================================================
     */

    var imageAnalysisReference by remember {
        mutableStateOf<ImageAnalysis?>(null)
    }

    /*
     * =========================================================
     * CAMERA THREAD
     * =========================================================
     */

    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    /*
     * =========================================================
     * İZİN İSTEME
     * =========================================================
     */

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted

            if (!granted) {

                errorMessage =
                    "Kamera izni verilmedi."
            }
        }

    /*
     * =========================================================
     * İLK AÇILIŞTA İZİN
     * =========================================================
     */

    LaunchedEffect(Unit) {

        if (!hasCameraPermission) {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    /*
     * =========================================================
     * 5 SANİYELİK OKUMA
     * =========================================================
     *
     * Kamera ilk geçerli sonucu gördüğünde başlar.
     *
     * 5 saniye boyunca analiz devam eder.
     *
     * 5 saniyenin sonunda:
     *
     * - Preview görüntüsü alınır
     * - Ekran dondurulur
     * - Kamera sonuçları artık değiştirilmez
     */

    LaunchedEffect(scanResult, isFrozen) {

        if (
            scanResult != null &&
            !isFrozen
        ) {

            countdown = 5

            for (second in 5 downTo 1) {

                if (isFrozen) {
                    break
                }

                countdown = second

                delay(1000)
            }

            if (!isFrozen) {

                val bitmap =
                    previewViewReference?.bitmap

                if (bitmap != null) {

                    capturedBitmap = bitmap
                }

                isFrozen = true
                countdown = 0
            }
        }
    }

    /*
     * =========================================================
     * EKRANDAN ÇIKARKEN CAMERA THREAD KAPAT
     * =========================================================
     */

    DisposableEffect(Unit) {

        onDispose {

            cameraProviderReference?.unbindAll()

            cameraExecutor.shutdown()
        }
    }

    /*
     * =========================================================
     * ANA EKRAN
     * =========================================================
     */

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black)
    ) {

        /*
         * =====================================================
         * KAMERA İZİNLİ
         * =====================================================
         */

        if (hasCameraPermission) {

            /*
             * -------------------------------------------------
             * CANLI KAMERA
             * -------------------------------------------------
             */

            AndroidView(

                modifier =
                    Modifier.fillMaxSize(),

                factory = { viewContext ->

                    val previewView =
                        PreviewView(viewContext)

                    previewViewReference =
                        previewView

                    val cameraProviderFuture =
                        ProcessCameraProvider.getInstance(
                            viewContext
                        )

                    cameraProviderFuture.addListener({

                        try {

                            val cameraProvider =
                                cameraProviderFuture.get()

                            cameraProviderReference =
                                cameraProvider

                            val preview =
                                Preview.Builder()
                                    .build()
                                    .also {

                                        it.surfaceProvider =
                                            previewView
                                                .surfaceProvider
                                    }

                            val imageAnalysis =
                                ImageAnalysis.Builder()
                                    .setBackpressureStrategy(
                                        ImageAnalysis
                                            .STRATEGY_KEEP_ONLY_LATEST
                                    )
                                    .build()

                            imageAnalysisReference =
                                imageAnalysis

                            val analyzer =
                                CameraAnalyzer { result ->

                                    /*
                                     * DONDURULMUŞSA
                                     * artık yeni sonuç alma.
                                     */

                                    if (!isFrozen) {

                                        scanResult =
                                            result

                                        /*
                                         * Sonucu dışarıya
                                         * göndermeye devam ediyoruz.
                                         *
                                         * Fakat ürün formuna
                                         * geçişi EKLE butonu
                                         * yapacak.
                                         */

                                    }
                                }

                            imageAnalysis.setAnalyzer(
                                cameraExecutor,
                                analyzer
                            )

                            val cameraSelector =
                                CameraSelector
                                    .DEFAULT_BACK_CAMERA

                            cameraProvider.unbindAll()

                            cameraProvider.bindToLifecycle(

                                lifecycleOwner,

                                cameraSelector,

                                preview,

                                imageAnalysis
                            )

                        } catch (exception: Exception) {

                            errorMessage =
                                exception.message
                                    ?: "Kamera başlatılamadı."
                        }

                    }, ContextCompat.getMainExecutor(viewContext))

                    previewView
                }
            )

            /*
             * =================================================
             * DONDURULMUŞ GÖRÜNTÜ
             * =================================================
             *
             * 5 saniye sonunda gerçek kamera görüntüsünü
             * ekranın üzerine koyuyoruz.
             *
             * Böylece kullanıcı hareketli kamera yerine
             * okuma anındaki sabit görüntüyü görür.
             */

            capturedBitmap?.let { bitmap ->

                Image(

                    bitmap = bitmap.asImageBitmap(),

                    contentDescription =
                        "Okunan ürün etiketi",

                    modifier =
                        Modifier.fillMaxSize()
                )
            }

            /*
             * =================================================
             * ÜST BAŞLIK
             * =================================================
             */

            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
            ) {

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(

                        text =
                            if (isFrozen) {
                                "OKUMA TAMAMLANDI"
                            } else {
                                "ETİKET OKU"
                            },

                        color = Color.White,

                        fontSize = 20.sp
                    )

                    Button(

                        onClick = onBack,

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    DarkRed
                            ),

                        shape =
                            RoundedCornerShape(12.dp)
                    ) {

                        Text(

                            text = "Geri",

                            color = Color.White
                        )
                    }
                }
            }

            /*
             * =================================================
             * TARAMA ALANI
             * =================================================
             */

            if (!isFrozen) {

                Box(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .padding(
                                horizontal = 32.dp
                            )
                            .align(
                                Alignment.Center
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text =
                            if (countdown > 0) {

                                "Okunuyor... $countdown"

                            } else {

                                "ETİKETİ BU ALANA GETİR"
                            },

                        modifier =
                            Modifier
                                .align(
                                    Alignment.BottomCenter
                                )
                                .padding(
                                    bottom = 8.dp
                                ),

                        color = Color.White,

                        fontSize = 15.sp
                    )
                }
            }

            /*
             * =================================================
             * SONUÇ PANELİ
             * =================================================
             */

            if (isFrozen) {

                scanResult?.let { result ->

                    ScanResultPanel(

                        result = result,

                        modifier =
                            Modifier
                                .align(
                                    Alignment.BottomCenter
                                )
                                .fillMaxWidth()
                                .padding(16.dp),

                        onAdd = {

                            /*
                             * KULLANICI EKLE DEDİĞİNDE
                             * sonuç ürün formuna gönderilir.
                             */

                            onScanResult(result)

                            onBack()
                        },

                        onRetry = {

                            /*
                             * YENİDEN OKUMA
                             */

                            capturedBitmap = null

                            scanResult = null

                            isFrozen = false

                            countdown = 0

                            errorMessage = null
                        }
                    )
                }
            }

            /*
             * =================================================
             * HENÜZ SONUÇ YOK
             * =================================================
             */

            if (
                !isFrozen &&
                scanResult == null
            ) {

                Text(

                    text =
                        "Etiketi kameranın ortasına getir",

                    modifier =
                        Modifier
                            .align(
                                Alignment.BottomCenter
                            )
                            .padding(
                                bottom = 32.dp
                            ),

                    color = Color.White,

                    fontSize = 13.sp
                )
            }

            /*
             * =================================================
             * HATA MESAJI
             * =================================================
             */

            errorMessage?.let { message ->

                Text(

                    text = message,

                    modifier =
                        Modifier
                            .align(
                                Alignment.BottomCenter
                            )
                            .padding(
                                horizontal = 20.dp,
                                vertical = 16.dp
                            ),

                    color = Color.White,

                    fontSize = 13.sp
                )
            }
        }

        /*
         * =====================================================
         * KAMERA İZNİ YOK
         * =====================================================
         */

        else {

            CameraPermissionScreen(

                onRequestPermission = {

                    permissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                },

                onBack = onBack
            )
        }
    }
}

/*
 * =============================================================
 * KAMERA İZİN EKRANI
 * =============================================================
 */

@Composable
private fun CameraPermissionScreen(

    onRequestPermission: () -> Unit,

    onBack: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFF5F5F5)
                )
                .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text = "📷",
            fontSize = 56.sp
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Text(

            text =
                "Kamera İzni Gerekli",

            fontSize = 22.sp,

            color = DarkRed
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(

            text =
                "Ürün etiketi ve barkodları okuyabilmek için kamera izni gerekiyor.",

            fontSize = 14.sp,

            color = Gray
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Button(

            onClick =
                onRequestPermission,

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        DarkRed
                )
        ) {

            Text(

                text =
                    "KAMERA İZNİ VER",

                color = Color.White
            )
        }

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Button(

            onClick = onBack,

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        Gray
                )
        ) {

            Text(

                text = "GERİ",

                color = Color.White
            )
        }
    }
}

/*
 * =============================================================
 * SONUÇ PANELİ
 * =============================================================
 */

@Composable
private fun ScanResultPanel(

    result: CameraScanResult,

    modifier: Modifier = Modifier,

    onAdd: () -> Unit,

    onRetry: () -> Unit
) {

    Column(

        modifier =
            modifier
                .background(

                    Color.White.copy(
                        alpha = 0.97f
                    ),

                    RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
    ) {

        Text(

            text =
                "OKUNAN BİLGİLER",

            fontSize = 16.sp,

            color = DarkRed
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        result.productCode?.let {

            ResultRow(

                title =
                    "Trendyol Ürün Kodu",

                value = it
            )
        }

        result.systemBarcode?.let {

            ResultRow(

                title =
                    "Sistem Barkodu",

                value = it
            )
        }

        result.color?.let {

            ResultRow(

                title =
                    "Renk",

                value = it
            )
        }

        result.size?.let {

            ResultRow(

                title =
                    "Beden",

                value = it
            )
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        /*
         * =====================================================
         * EKLE
         * =====================================================
         */

        Button(

            onClick = onAdd,

            modifier =
                Modifier.fillMaxWidth(),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        DarkRed
                ),

            shape =
                RoundedCornerShape(12.dp)
        ) {

            Text(

                text =
                    "✓  EKLE",

                color =
                    Color.White,

                fontSize = 15.sp
            )
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        /*
         * =====================================================
         * TEKRAR OKU
         * =====================================================
         */

        Button(

            onClick = onRetry,

            modifier =
                Modifier.fillMaxWidth(),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        Gray
                ),

            shape =
                RoundedCornerShape(12.dp)
        ) {

            Text(

                text =
                    "↻  TEKRAR OKU",

                color =
                    Color.White,

                fontSize = 14.sp
            )
        }
    }
}

/*
 * =============================================================
 * SONUÇ SATIRI
 * =============================================================
 */

@Composable
private fun ResultRow(

    title: String,

    value: String
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 3.dp
                ),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(

            text = title,

            fontSize = 12.sp,

            color = Gray
        )

        Text(

            text = value,

            fontSize = 13.sp,

            color = SteelBlue
        )
    }
}