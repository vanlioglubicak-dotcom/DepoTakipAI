package com.example.depotakipai.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.depotakipai.domain.model.CameraScanBatch
import com.example.depotakipai.domain.model.CameraScanItem
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executors

private val StormBlue = Color(0xFF34506D)
private val BlueSlate = Color(0xFF557392)
private val DuskBlue = Color(0xFF7A8CA6)
private val FoggyBlue = Color(0xFFA1B2C4)
private val DarkOverlay = Color.Black.copy(alpha = 0.48f)

@Composable
fun ContinuousCameraScanScreen(
    onBack: () -> Unit = {},
    onFinished: (CameraScanBatch) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /*
     * Onaylanmış tarama listesi.
     */
    var scanBatch by remember {
        mutableStateOf(CameraScanBatch())
    }

    /*
     * Kullanıcıya onaylatılmayı bekleyen ürün.
     */
    var pendingItem by remember {
        mutableStateOf<CameraScanItem?>(null)
    }

    var pendingProductCode by remember {
        mutableStateOf<String?>(null)
    }

    var pendingQuantity by remember {
        mutableStateOf(1)
    }

    /*
     * Son okunan ürün.
     */
    var lastDetectedValue by remember {
        mutableStateOf<String?>(null)
    }

    var lastDetectedTime by remember {
        mutableStateOf(0L)
    }

    var scanSuccess by remember {
        mutableStateOf(false)
    }

    var errorText by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * Barkod ve OCR aynı etiket için kısa süre içinde
     * ayrı sonuç verebilir.
     */
    var pendingBarcodeText by remember {
        mutableStateOf<String?>(null)
    }

    var pendingBarcodeTime by remember {
        mutableStateOf(0L)
    }

    var showReadList by remember {
        mutableStateOf(false)
    }

    val currentPendingItem by rememberUpdatedState(pendingItem)
    val currentLastDetectedValue by rememberUpdatedState(lastDetectedValue)
    val currentLastDetectedTime by rememberUpdatedState(lastDetectedTime)

    var cameraProvider by remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    val toneGenerator = remember {
        ToneGenerator(
            AudioManager.STREAM_NOTIFICATION,
            90
        )
    }

    val vibrator = remember {
        context.getSystemService(
            Context.VIBRATOR_SERVICE
        ) as? Vibrator
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
            cameraExecutor.shutdown()
            toneGenerator.release()
        }
    }

    /*
     * Okuma başarılı olduğunda ses + titreşim.
     */
    fun playScanFeedback() {
        try {
            toneGenerator.startTone(
                ToneGenerator.TONE_PROP_BEEP,
                130
            )
        } catch (_: Exception) {
        }

        try {
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(
                        VibrationEffect.createOneShot(
                            80L,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(80L)
                }
            }
        } catch (_: Exception) {
        }
    }

    /*
     * OCR gelmezse barkod tek başına ürün oluşturabilir.
     */
    LaunchedEffect(pendingBarcodeTime) {
        val barcodeText = pendingBarcodeText
        val barcodeTime = pendingBarcodeTime

        if (
            barcodeText.isNullOrBlank() ||
            barcodeTime == 0L
        ) {
            return@LaunchedEffect
        }

        kotlinx.coroutines.delay(1000L)

        if (
            pendingBarcodeTime == barcodeTime &&
            pendingItem == null
        ) {
            processDetectedText(
                rawText = barcodeText,
                currentBatch = scanBatch,
                lastValue = lastDetectedValue,
                lastTime = lastDetectedTime,
                requireDetails = false,
                onAccepted = { item, value, quantity ->
                    pendingItem = item
                    pendingProductCode = value
                    pendingQuantity = quantity

                    lastDetectedValue = value
                    lastDetectedTime =
                        System.currentTimeMillis()

                    scanSuccess = true
                    playScanFeedback()

                    pendingBarcodeText = null
                    pendingBarcodeTime = 0L
                },
                onRejected = {
                    pendingBarcodeText = null
                    pendingBarcodeTime = 0L
                    pendingQuantity = 1
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        /*
         * =========================================================
         * KAMERA
         * =========================================================
         */
        if (hasCameraPermission) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { viewContext ->

                    val previewView =
                        PreviewView(viewContext)

                    val providerFuture =
                        ProcessCameraProvider.getInstance(
                            viewContext
                        )

                    providerFuture.addListener({

                        try {

                            val provider =
                                providerFuture.get()

                            cameraProvider = provider

                            val preview =
                                Preview.Builder()
                                    .build()
                                    .also {
                                        it.surfaceProvider =
                                            previewView.surfaceProvider
                                    }

                            val analysis =
                                ImageAnalysis.Builder()
                                    .setBackpressureStrategy(
                                        ImageAnalysis
                                            .STRATEGY_KEEP_ONLY_LATEST
                                    )
                                    .build()

                            val barcodeScanner =
                                BarcodeScanning.getClient()

                            val textRecognizer =
                                TextRecognition.getClient(
                                    TextRecognizerOptions
                                        .DEFAULT_OPTIONS
                                )

                            analysis.setAnalyzer(
                                cameraExecutor
                            ) { imageProxy ->

                                val mediaImage =
                                    imageProxy.image

                                if (mediaImage == null) {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }

                                val inputImage =
                                    InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )

                                /*
                                 * BARKOD
                                 */
                                barcodeScanner
                                    .process(inputImage)
                                    .addOnSuccessListener { barcodes ->

                                        val barcodeValue =
                                            barcodes
                                                .firstOrNull()
                                                ?.rawValue

                                        if (
                                            !barcodeValue.isNullOrBlank() &&
                                            currentPendingItem == null
                                        ) {
                                            pendingBarcodeText =
                                                barcodeValue

                                            pendingBarcodeTime =
                                                System.currentTimeMillis()
                                        }
                                    }
                                    .addOnFailureListener {
                                        // Kamera çalışmaya devam eder.
                                    }

                                /*
                                 * OCR
                                 */
                                textRecognizer
                                    .process(inputImage)
                                    .addOnSuccessListener { result ->

                                        val detectedText =
                                            result.text

                                        if (detectedText.isBlank()) {
                                            return@addOnSuccessListener
                                        }

                                        /*
                                         * =================================================
                                         * ÖNEMLİ DÜZELTME
                                         * =================================================
                                         *
                                         * Ürün kodu ilk karede okunmuş olabilir.
                                         * Ancak RENK/BEDEN ikinci karede okunabilir.
                                         *
                                         * Artık pendingItem varsa OCR'ı kesmiyoruz.
                                         * Yeni kareden gelen renk ve beden mevcut ürüne
                                         * ekleniyor.
                                         */
                                        val existingItem =
                                            currentPendingItem

                                        if (existingItem != null) {

                                            val normalizedOcr =
                                                detectedText
                                                    .uppercase(
                                                        Locale(
                                                            "tr",
                                                            "TR"
                                                        )
                                                    )
                                                    .replace(
                                                        "\n",
                                                        " "
                                                    )

                                            val detectedColor =
                                                extractColor(
                                                    normalizedOcr
                                                )

                                            val detectedSize =
                                                extractSize(
                                                    normalizedOcr
                                                )

                                            val detectedQuantity =
                                                extractQuantity(
                                                    normalizedOcr
                                                )

                                            val mergedColor =
                                                if (
                                                    detectedColor !=
                                                    "BELİRSİZ"
                                                ) {
                                                    detectedColor
                                                } else {
                                                    existingItem.color
                                                }

                                            val mergedSize =
                                                if (
                                                    detectedSize !=
                                                    "BELİRSİZ"
                                                ) {
                                                    detectedSize
                                                } else {
                                                    existingItem.size
                                                }

                                            /*
                                             * Adet bilgisi de sonradan
                                             * okunabilsin.
                                             */
                                            if (
                                                detectedQuantity > 1
                                            ) {
                                                pendingQuantity =
                                                    detectedQuantity
                                            }

                                            /*
                                             * Renk veya beden bulunduysa
                                             * mevcut ürünü güncelle.
                                             */
                                            if (
                                                mergedColor !=
                                                existingItem.color ||
                                                mergedSize !=
                                                existingItem.size
                                            ) {

                                                pendingItem =
                                                    existingItem.copy(
                                                        color =
                                                            mergedColor,
                                                        size =
                                                            mergedSize
                                                    )
                                            }

                                            /*
                                             * Ürün zaten bulundu.
                                             * Bu kareyi sadece metadata
                                             * tamamlamak için kullandık.
                                             */
                                            return@addOnSuccessListener
                                        }

                                        /*
                                         * Barkod + OCR birleştirme.
                                         */
                                        val barcodeText =
                                            pendingBarcodeText

                                        val barcodeTime =
                                            pendingBarcodeTime

                                        val barcodeIsRecent =
                                            !barcodeText
                                                .isNullOrBlank() &&
                                                    barcodeTime > 0L &&
                                                    System.currentTimeMillis() -
                                                    barcodeTime < 1500L

                                        val combinedText =
                                            if (
                                                barcodeIsRecent
                                            ) {
                                                "$barcodeText $detectedText"
                                            } else {
                                                detectedText
                                            }

                                        /*
                                         * OCR tarafında artık ürün kodu
                                         * tek başına kabul edilmiyor.
                                         *
                                         * Renk + beden de okunmuşsa
                                         * doğrudan kabul ediyoruz.
                                         */
                                        processDetectedText(
                                            rawText = combinedText,
                                            currentBatch = scanBatch,
                                            lastValue =
                                                currentLastDetectedValue,
                                            lastTime =
                                                currentLastDetectedTime,
                                            requireDetails = true,
                                            onAccepted = {
                                                    item,
                                                    value,
                                                    quantity ->

                                                pendingItem =
                                                    item

                                                pendingProductCode =
                                                    value

                                                pendingQuantity =
                                                    quantity

                                                lastDetectedValue =
                                                    value

                                                lastDetectedTime =
                                                    System.currentTimeMillis()

                                                scanSuccess =
                                                    true

                                                playScanFeedback()

                                                pendingBarcodeText =
                                                    null

                                                pendingBarcodeTime =
                                                    0L
                                            },
                                            onRejected = {
                                                /*
                                                 * Henüz renk veya beden
                                                 * okunmamış olabilir.
                                                 * Bir sonraki OCR karesini bekle.
                                                 */
                                            }
                                        )
                                    }
                                    .addOnFailureListener {
                                        /*
                                         * OCR başarısız olsa bile
                                         * kamera devam eder.
                                         */
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            }

                            provider.unbindAll()

                            provider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                analysis
                            )

                        } catch (exception: Exception) {

                            errorText =
                                exception.message
                                    ?: "Kamera başlatılamadı."
                        }

                    }, ContextCompat.getMainExecutor(viewContext))

                    previewView
                }
            )

        } else {

            /*
             * KAMERA İZNİ
             */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = "Kamera izni gerekli",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = {
                        permissionLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StormBlue
                    )
                ) {
                    Text("KAMERA İZNİ VER")
                }
            }
        }

        /*
         * =========================================================
         * ÜST BİLGİ BUTONLARI
         * =========================================================
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
                value =
                    scanBatch.totalQuantity.toString(),
                color = StormBlue,
                onClick = {
                    showReadList = true
                }
            )

            ScanInfoButton(
                modifier = Modifier.weight(1f),
                title = "FARKLI ÜRÜN",
                value =
                    scanBatch
                        .differentProductCount
                        .toString(),
                color = FoggyBlue
            )
        }

        /*
         * =========================================================
         * TARAMA ALANI
         * =========================================================
         */

        ScanAreaOverlay(
            success = scanSuccess
        )

        /*
         * =========================================================
         * OKUNAN ÜRÜN
         * =========================================================
         */

        pendingItem?.let { item ->

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 300.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(
                                alpha = 0.96f
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(
                            horizontal = 24.dp,
                            vertical = 14.dp
                        )
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "ÜRÜN OKUNDU",
                            color = BlueSlate,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                pendingProductCode
                                    ?: item.productCode,
                            color = StormBlue,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                "Renk: ${item.color}",
                            color = Color.DarkGray,
                            fontSize = 13.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                "Beden: ${item.size}",
                            color = Color.DarkGray,
                            fontSize = 13.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                "Adet: $pendingQuantity",
                            color = StormBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        /*
         * =========================================================
         * REDDET / ONAYLA
         * =========================================================
         */

        if (pendingItem != null) {

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 96.dp
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                /*
                 * REDDET
                 */
                Button(
                    modifier =
                        Modifier.weight(1f),
                    onClick = {

                        pendingItem = null
                        pendingProductCode = null
                        pendingQuantity = 1

                        scanSuccess = false

                        lastDetectedValue = null
                        lastDetectedTime = 0L

                        pendingBarcodeText = null
                        pendingBarcodeTime = 0L
                    },
                    shape =
                        RoundedCornerShape(14.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                FoggyBlue,
                            contentColor =
                                StormBlue
                        )
                ) {

                    Text(
                        text = "REDDET",
                        color = StormBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                /*
                 * ONAYLA
                 */
                Button(
                    modifier =
                        Modifier.weight(1f),
                    onClick = {

                        val confirmedItem =
                            pendingItem
                                ?: return@Button

                        val quantity =
                            pendingQuantity
                                .coerceAtLeast(1)

                        /*
                         * ADET kadar fiziksel kayıt oluştur.
                         */
                        val confirmedItems =
                            List(quantity) {

                                confirmedItem.copy(
                                    id =
                                        UUID
                                            .randomUUID()
                                            .toString(),
                                    scannedAt =
                                        System
                                            .currentTimeMillis()
                                )
                            }

                        scanBatch =
                            scanBatch.addItems(
                                confirmedItems
                            )

                        pendingItem = null
                        pendingProductCode = null
                        pendingQuantity = 1

                        scanSuccess = false

                        lastDetectedValue = null
                        lastDetectedTime = 0L

                        pendingBarcodeText = null
                        pendingBarcodeTime = 0L
                    },
                    shape =
                        RoundedCornerShape(14.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                BlueSlate,
                            contentColor =
                                Color.White
                        )
                ) {

                    Text(
                        text = "ONAYLA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        /*
         * =========================================================
         * TARAMAYI BİTİR
         * =========================================================
         */

        if (pendingItem == null) {

            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 96.dp),
                onClick = {
                    onFinished(scanBatch)
                },
                shape =
                    RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            StormBlue.copy(
                                alpha = 0.90f
                            )
                    )
            ) {

                Text(
                    text = "TARAMAYI BİTİR",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        /*
         * GERİ
         */
        Button(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(
                    start = 16.dp,
                    top = 58.dp
                ),
            onClick = onBack,
            shape =
                RoundedCornerShape(12.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        StormBlue.copy(
                            alpha = 0.88f
                        )
                )
        ) {

            Text(
                text = "GERİ",
                color = Color.White,
                fontSize = 13.sp
            )
        }

        /*
         * HATA
         */
        errorText?.let { message ->

            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 155.dp),
                text = message,
                color = Color.White,
                fontSize = 13.sp
            )
        }

        /*
         * OKUNANLAR LİSTESİ
         */
        if (showReadList) {

            ReadProductListOverlay(
                batch = scanBatch,
                onClose = {
                    showReadList = false
                }
            )
        }
    }
}

/**
 * OKUNAN / FARKLI ÜRÜN bilgi butonu.
 */
@Composable
private fun ScanInfoButton(
    modifier: Modifier,
    title: String,
    value: String,
    color: Color,
    onClick: () -> Unit = {}
) {

    Box(
        modifier = modifier
            .clickable(
                onClick = onClick
            )
            .background(
                StormBlue.copy(
                    alpha = 0.92f
                ),
                RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color =
                    color.copy(alpha = 0.8f),
                shape =
                    RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        contentAlignment =
            Alignment.Center
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )

            Box(
                modifier = Modifier
                    .background(
                        color,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 3.dp
                    )
            ) {

                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Merkezi kamera okuma alanı.
 */
@Composable
private fun ScanAreaOverlay(
    success: Boolean
) {

    Box(
        modifier =
            Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(
                    width = 320.dp,
                    height = 180.dp
                )
                .border(
                    width = 5.dp,
                    color =
                        if (success) {
                            BlueSlate
                        } else {
                            DuskBlue
                        },
                    shape =
                        RoundedCornerShape(0.dp)
                )
        )
    }
}

/**
 * OKUNAN ÜRÜNLER listesini gösterir.
 */
@Composable
private fun ReadProductListOverlay(
    batch: CameraScanBatch,
    onClose: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                StormBlue.copy(
                    alpha = 0.88f
                )
            )
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 90.dp,
                bottom = 90.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White,
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
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
                    text = "OKUNAN ÜRÜNLER",
                    color = StormBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onClose,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                StormBlue
                        ),
                    shape =
                        RoundedCornerShape(10.dp)
                ) {

                    Text(
                        text = "KAPAT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            if (batch.items.isEmpty()) {

                Text(
                    text =
                        "Henüz onaylanmış ürün yok.",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )

            } else {

                batch.groupByProductCode()
                    .forEach { productSummary ->

                        val variants =
                            batch.groupByColorAndSize(
                                productSummary.productCode
                            )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom = 10.dp
                                )
                                .background(
                                    FoggyBlue.copy(alpha = 0.18f),
                                    RoundedCornerShape(
                                        10.dp
                                    )
                                )
                                .padding(12.dp)
                        ) {

                            Text(
                                text =
                                    productSummary.productCode,
                                color = StormBlue,
                                fontSize = 16.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            variants.forEach { variant ->

                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            5.dp
                                        )
                                )

                                Text(
                                    text =
                                        "Renk: ${variant.color}    " +
                                                "Beden: ${variant.size}    " +
                                                "Adet: ${variant.quantity}",
                                    color =
                                        Color.DarkGray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
            }
        }
    }
}

/**
 * OCR / barkod metnini ürüne dönüştürür.
 */
private fun processDetectedText(
    rawText: String,
    currentBatch: CameraScanBatch,
    lastValue: String?,
    lastTime: Long,
    requireDetails: Boolean = false,
    onAccepted:
        (CameraScanItem, String, Int) -> Unit,
    onRejected: () -> Unit
) {

    val normalizedText =
        rawText
            .uppercase(
                Locale(
                    "tr",
                    "TR"
                )
            )
            .replace(
                "\n",
                " "
            )

    val productRegex =
        Regex(
            pattern =
                "(SNZ|MTS|FS)[\\s-]?(\\d{3,8})"
        )

    val match =
        productRegex.find(
            normalizedText
        )

    if (match == null) {
        onRejected()
        return
    }

    val brand =
        match.groupValues[1]

    val number =
        match.groupValues[2]

    val productCode =
        "$brand-$number"

    val now =
        System.currentTimeMillis()

    /*
     * Aynı etiketi kısa sürede tekrar
     * kabul etme.
     */
    if (
        lastValue == productCode &&
        now - lastTime < 1500L
    ) {
        return
    }

    val color =
        extractColor(
            normalizedText
        )

    val size =
        extractSize(
            normalizedText
        )

    val quantity =
        extractQuantity(
            normalizedText
        )

    /*
     * OCR tarafında ürün kodu tek başına
     * yeterli değil.
     *
     * Renk + beden gelmesini bekle.
     */
    if (
        requireDetails &&
        (
                color == "BELİRSİZ" ||
                        size == "BELİRSİZ"
                )
    ) {
        onRejected()
        return
    }

    /*
     * SNZ-2926
     *
     * 00002926
     */
    val numericPart =
        number
            .takeLast(8)
            .padStart(
                8,
                '0'
            )

    val item =
        CameraScanItem(
            productCode =
                productCode,
            systemBarcode =
                numericPart,
            color =
                color,
            size =
                size
        )

    onAccepted(
        item,
        productCode,
        quantity
    )
}

/**
 * ADET bilgisini çıkarır.
 */
private fun extractQuantity(
    text: String
): Int {

    val labeledPattern =
        Regex(
            pattern =
                """(?:ADET|MIKTAR|MİKTAR|QTY)\s*[:\-]?\s*(\d{1,3})\b"""
        )

    val labeledMatch =
        labeledPattern.find(
            text
        )

    return labeledMatch
        ?.groupValues
        ?.getOrNull(1)
        ?.toIntOrNull()
        ?.coerceIn(
            1,
            999
        )
        ?: 1
}

/**
 * Renk bilgisini çıkarır.
 */
private fun extractColor(
    text: String
): String {

    val colors =
        listOf(
            "SİYAH",
            "BEYAZ",
            "BORDO",
            "KIRMIZI",
            "LACİVERT",
            "MAVİ",
            "YEŞİL",
            "GRİ",
            "BEJ",
            "KAHVERENGİ",
            "PEMBE",
            "EKRU",
            "TURUNCU",
            "MOR"
        )

    return colors.firstOrNull {
        text.contains(it)
    } ?: "BELİRSİZ"
}

/**
 * Beden bilgisini çıkarır.
 */
private fun extractSize(
    text: String
): String {

    val sizes =
        listOf(
            "XXXL",
            "XXL",
            "XL",
            "XS",
            "S",
            "M",
            "L"
        )

    sizes.forEach { size ->

        val regex =
            Regex(
                pattern =
                    "\\b${Regex.escape(size)}\\b"
            )

        if (
            regex.containsMatchIn(text)
        ) {
            return size
        }
    }

    return "BELİRSİZ"
}