package com.example.depotakipai.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

private val DarkRed = Color(0xFF8B0000)
private val Gray = Color(0xFF808080)

@Composable
fun CameraScanScreen(
    onBack: () -> Unit = {},
    onScanResult: (CameraScanResult) -> Unit = {},
    mode: CameraScanMode = CameraScanMode.PRODUCT
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // =========================================================
    // KAMERA İZNİ
    // =========================================================

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

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    // =========================================================
    // ÇEKİLEN FOTOĞRAF
    // =========================================================

    var capturedBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    // =========================================================
    // ANALİZ DURUMU
    // =========================================================

    var isAnalyzing by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    // =========================================================
    // CAMERA PROVIDER
    // =========================================================

    var cameraProvider by remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    // =========================================================
    // IMAGE CAPTURE
    // =========================================================

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    // =========================================================
    // FOTOĞRAF ALINDI MI?
    // =========================================================

    val photoTaken = remember {
        AtomicBoolean(false)
    }

    // =========================================================
    // CAMERA EXECUTOR
    // =========================================================

    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    // =========================================================
    // TEMİZLE
    // =========================================================

    DisposableEffect(Unit) {

        onDispose {

            cameraProvider?.unbindAll()

            cameraExecutor.shutdown()
        }
    }

    // =========================================================
    // ANA EKRAN
    // =========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // =====================================================
        // CANLI KAMERA
        // =====================================================

        if (
            hasCameraPermission &&
            capturedBitmap == null
        ) {

            /*
             * capturedBitmap değiştiğinde AndroidView'ın
             * yeniden oluşturulmasını sağlıyoruz.
             *
             * Böylece X ile fotoğraf iptal edildiğinde
             * kamera gerçekten yeniden başlar.
             */

            key(capturedBitmap == null) {

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

                                cameraProvider =
                                    provider

                                // =================================
                                // PREVIEW
                                // =================================

                                val preview =
                                    Preview.Builder()
                                        .build()
                                        .also {

                                            it.surfaceProvider =
                                                previewView
                                                    .surfaceProvider
                                        }

                                // =================================
                                // FOTOĞRAF ÇEKME
                                // =================================

                                val capture =
                                    ImageCapture.Builder()
                                        .setCaptureMode(
                                            ImageCapture
                                                .CAPTURE_MODE_MINIMIZE_LATENCY
                                        )
                                        .build()

                                imageCapture =
                                    capture

                                // =================================
                                // BARKOD ANALİZİ
                                // =================================

                                val imageAnalysis =
                                    ImageAnalysis.Builder()
                                        .setBackpressureStrategy(
                                            ImageAnalysis
                                                .STRATEGY_KEEP_ONLY_LATEST
                                        )
                                        .build()

                                val barcodeAnalyzer =
                                    BarcodeCaptureAnalyzer {

                                        /*
                                         * Barkod ilk kez
                                         * görüldüğünde fotoğraf çek.
                                         */

                                        if (
                                            photoTaken
                                                .compareAndSet(
                                                    false,
                                                    true
                                                )
                                        ) {

                                            capturePhoto(

                                                imageCapture =
                                                    capture,

                                                context =
                                                    viewContext,

                                                mode =
                                                    mode,

                                                onBitmapReady = { bitmap ->

                                                    capturedBitmap =
                                                        bitmap

                                                    /*
                                                     * Canlı kamerayı durdur.
                                                     */

                                                    provider
                                                        .unbindAll()
                                                },

                                                onError = {

                                                    photoTaken
                                                        .set(false)

                                                    errorMessage =
                                                        "Fotoğraf alınamadı."
                                                }
                                            )
                                        }
                                    }

                                imageAnalysis.setAnalyzer(
                                    cameraExecutor,
                                    barcodeAnalyzer
                                )

                                // =================================
                                // ARKA KAMERA
                                // =================================

                                val cameraSelector =
                                    CameraSelector
                                        .DEFAULT_BACK_CAMERA

                                provider.unbindAll()

                                provider.bindToLifecycle(

                                    lifecycleOwner,

                                    cameraSelector,

                                    preview,

                                    capture,

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
            }
        }

        // =====================================================
        // ÇEKİLEN FOTOĞRAF
        // =====================================================

        capturedBitmap?.let { bitmap ->

            Image(

                bitmap =
                    bitmap.asImageBitmap(),

                contentDescription =
                    "Çekilen etiket fotoğrafı",

                modifier =
                    Modifier.fillMaxSize()
            )
        }

        // =====================================================
        // ÜST BAŞLIK
        // =====================================================

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(

                text =
                    when {

                        capturedBitmap != null ->
                            "FOTOĞRAF"

                        mode == CameraScanMode.INCOMING_RETURN ->
                            "GELEN İADE"

                        else ->
                            "ETİKET OKU"
                    },

                color =
                    Color.White,

                fontSize =
                    24.sp,

                fontWeight =
                    FontWeight.Medium
            )

            if (capturedBitmap == null) {

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
                        text = "Geri"
                    )
                }
            }
        }

        // =====================================================
        // FOTOĞRAF ÇEKİLMEDEN ÖNCE
        // =====================================================

        if (
            capturedBitmap == null &&
            hasCameraPermission
        ) {

            Text(

                text =
                    if (
                        mode == CameraScanMode.INCOMING_RETURN
                    ) {
                        "İade etiketini kameraya gösterin"
                    } else {
                        "Etiketi kameraya gösterin"
                    },

                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(top = 250.dp),

                color =
                    Color.White,

                fontSize =
                    15.sp
            )
        }

        // =====================================================
        // ANALİZ EDİLİYOR
        // =====================================================

        if (isAnalyzing) {

            Box(

                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                androidx.compose.material3.Card(

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        androidx.compose.material3
                            .CardDefaults
                            .cardColors(
                                containerColor =
                                    Color.Black.copy(
                                        alpha = 0.80f
                                    )
                            )
                ) {

                    Text(

                        text =
                            "Etiket analiz ediliyor...",

                        modifier =
                            Modifier.padding(
                                horizontal = 28.dp,
                                vertical = 20.dp
                            ),

                        color =
                            Color.White,

                        fontSize =
                            17.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }

        // =====================================================
        // ALT X / ✓
        // =====================================================

        if (
            capturedBitmap != null &&
            !isAnalyzing
        ) {

            Row(

                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(20.dp),

                horizontalArrangement =
                    Arrangement.spacedBy(24.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // =============================================
                // X — İPTAL
                // =============================================

                Button(

                    onClick = {

                        /*
                         * Geçici bitmap bellekte tutuluyor.
                         * Kamera yeniden başlatılıyor.
                         */

                        capturedBitmap =
                            null

                        isAnalyzing =
                            false

                        errorMessage =
                            null

                        imageCapture =
                            null

                        cameraProvider =
                            null

                        photoTaken.set(false)
                    },

                    modifier =
                        Modifier.weight(1f),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Gray
                        ),

                    shape =
                        RoundedCornerShape(18.dp)
                ) {

                    Text(

                        text = "✕",

                        fontSize = 30.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }

                // =============================================
                // ✓ — SEÇ
                // =============================================

                Button(

                    onClick = {

                        val bitmap =
                            capturedBitmap

                        if (bitmap == null) {

                            return@Button
                        }

                        /*
                         * Kullanıcı fotoğrafı seçti.
                         *
                         * Şimdi gerçek analiz başlıyor.
                         */

                        isAnalyzing =
                            true

                        errorMessage =
                            null

                        val analyzer =
                            CapturedLabelAnalyzer()

                        analyzer.analyze(

                            bitmap = bitmap,

                            onResult = { result ->

                                /*
                                 * Analiz tamamlandı.
                                 *
                                 * Sonuç artık AppNavigation'a
                                 * gönderilebilir.
                                 */

                                isAnalyzing =
                                    false

                                onScanResult(
                                    result
                                )
                            },

                            onError = { message ->

                                isAnalyzing =
                                    false

                                errorMessage =
                                    message
                            }
                        )
                    },

                    modifier =
                        Modifier.weight(1f),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                DarkRed
                        ),

                    shape =
                        RoundedCornerShape(18.dp)
                ) {

                    Text(

                        text = "✓",

                        fontSize = 30.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }

        // =====================================================
        // HATA MESAJI
        // =====================================================

        errorMessage?.let { message ->

            androidx.compose.material3.Card(

                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 105.dp
                        ),

                shape =
                    RoundedCornerShape(12.dp),

                colors =
                    androidx.compose.material3
                        .CardDefaults
                        .cardColors(
                            containerColor =
                                DarkRed
                        )
            ) {

                Text(

                    text = message,

                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 12.dp
                        ),

                    color =
                        Color.White,

                    fontSize =
                        14.sp
                )
            }
        }
    }
}

// =============================================================
// FOTOĞRAF ÇEKME
// =============================================================

private fun capturePhoto(
    imageCapture: ImageCapture,
    context: Context,
    mode: CameraScanMode,
    onBitmapReady: (Bitmap) -> Unit,
    onError: () -> Unit
) {

    /*
     * GELEN İADE MODU
     *
     * Fotoğraf MediaStore'a kaydedilmez.
     *
     * Bunun yerine uygulamanın geçici cache klasörüne
     * alınır, bitmap olarak okunur ve hemen silinir.
     */

    if (mode == CameraScanMode.INCOMING_RETURN) {

        val temporaryFile =
            File(
                context.cacheDir,
                "incoming_return_scan.jpg"
            )

        val outputOptions =
            ImageCapture.OutputFileOptions
                .Builder(
                    temporaryFile
                )
                .build()

        imageCapture.takePicture(

            outputOptions,

            ContextCompat.getMainExecutor(context),

            object :
                ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(
                    outputFileResults:
                    ImageCapture.OutputFileResults
                ) {

                    try {

                        val bitmap =
                            BitmapFactory.decodeFile(
                                temporaryFile.absolutePath
                            )

                        temporaryFile.delete()

                        if (bitmap != null) {

                            onBitmapReady(
                                bitmap
                            )

                        } else {

                            onError()
                        }

                    } catch (_: Exception) {

                        temporaryFile.delete()

                        onError()
                    }
                }

                override fun onError(
                    exception:
                    ImageCaptureException
                ) {

                    temporaryFile.delete()

                    onError()
                }
            }
        )

        return
    }

    // =========================================================
    // ANA ÜRÜN MODU
    // =========================================================

    val fileName =
        "depo_etiket_${
            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.US
            ).format(Date())
        }.jpg"

    val contentValues =
        ContentValues().apply {

            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                fileName
            )

            put(
                MediaStore.MediaColumns.MIME_TYPE,
                "image/jpeg"
            )

            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES +
                        "/DepoTakipAI"
            )
        }

    val outputOptions =
        ImageCapture.OutputFileOptions.Builder(

            context.contentResolver,

            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

            contentValues

        ).build()

    imageCapture.takePicture(

        outputOptions,

        ContextCompat.getMainExecutor(context),

        object :
            ImageCapture.OnImageSavedCallback {

            override fun onImageSaved(
                outputFileResults:
                ImageCapture.OutputFileResults
            ) {

                try {

                    val uri =
                        outputFileResults.savedUri
                            ?: run {

                                onError()

                                return
                            }

                    val inputStream =
                        context.contentResolver
                            .openInputStream(uri)

                    val bitmap =
                        inputStream?.use {

                            BitmapFactory.decodeStream(it)
                        }

                    if (bitmap != null) {

                        onBitmapReady(
                            bitmap
                        )

                    } else {

                        onError()
                    }

                } catch (_: Exception) {

                    onError()
                }
            }

            override fun onError(
                exception:
                ImageCaptureException
            ) {

                onError()
            }
        }
    )
}