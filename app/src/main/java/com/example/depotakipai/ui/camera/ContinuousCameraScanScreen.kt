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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import java.util.concurrent.Executors

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val RejectRed = Color(0xFFB3261E)
private val SuccessGreen = Color(0xFF2E7D32)
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
     *
     * Barkod okunduğu anda doğrudan listeye eklenmez.
     * Önce ONAYLA / REDDET gösterilir.
     */
    var pendingItem by remember {
        mutableStateOf<CameraScanItem?>(null)
    }

    var pendingProductCode by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * Son okunan barkod.
     * Aynı etiketin arka arkaya tekrar okunmasını engeller.
     */
    var lastDetectedValue by remember {
        mutableStateOf<String?>(null)
    }

    var lastDetectedTime by remember {
        mutableStateOf(0L)
    }

    /*
     * Okuma animasyonu için.
     */
    var scanSuccess by remember {
        mutableStateOf(false)
    }

    var errorText by remember {
        mutableStateOf<String?>(null)
    }

    var cameraProvider by remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    /*
     * Barkod okununca ses.
     */
    val toneGenerator = remember {
        ToneGenerator(
            AudioManager.STREAM_NOTIFICATION,
            90
        )
    }

    /*
     * Titreşim.
     */
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
     * Okuma başarılı olduğunda kısa ses + titreşim.
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        /*
         * KAMERA
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

                                /*
                                 * Kullanıcı henüz önceki ürünü
                                 * onaylamadıysa yeni ürün alma.
                                 */
                                if (pendingItem != null) {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }

                                val inputImage =
                                    InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy
                                            .imageInfo
                                            .rotationDegrees
                                    )

                                /*
                                 * BARKOD OKUMA
                                 */
                                barcodeScanner
                                    .process(inputImage)
                                    .addOnSuccessListener { barcodes ->

                                        val barcodeValue =
                                            barcodes
                                                .firstOrNull()
                                                ?.rawValue

                                        if (
                                            !barcodeValue
                                                .isNullOrBlank()
                                        ) {

                                            processDetectedText(
                                                rawText = barcodeValue,
                                                currentBatch = scanBatch,
                                                lastValue = lastDetectedValue,
                                                lastTime = lastDetectedTime,
                                                onAccepted = { item, value ->

                                                    /*
                                                     * Önce bekleyen ürün
                                                     * olarak göster.
                                                     */
                                                    pendingItem = item
                                                    pendingProductCode =
                                                        value

                                                    lastDetectedValue =
                                                        value

                                                    lastDetectedTime =
                                                        System.currentTimeMillis()

                                                    scanSuccess = true

                                                    playScanFeedback()
                                                },
                                                onRejected = {
                                                    /*
                                                     * Sessizce devam et.
                                                     */
                                                }
                                            )
                                        }
                                    }
                                    .addOnFailureListener {
                                        /*
                                         * Kamera çalışmaya devam eder.
                                         */
                                    }

                                /*
                                 * OCR OKUMA
                                 */
                                textRecognizer
                                    .process(inputImage)
                                    .addOnSuccessListener { result ->

                                        val detectedText =
                                            result.text

                                        if (
                                            detectedText.isNotBlank()
                                        ) {

                                            processDetectedText(
                                                rawText = detectedText,
                                                currentBatch = scanBatch,
                                                lastValue = lastDetectedValue,
                                                lastTime = lastDetectedTime,
                                                onAccepted = { item, value ->

                                                    pendingItem = item
                                                    pendingProductCode =
                                                        value

                                                    lastDetectedValue =
                                                        value

                                                    lastDetectedTime =
                                                        System.currentTimeMillis()

                                                    scanSuccess = true

                                                    playScanFeedback()
                                                },
                                                onRejected = {
                                                    /*
                                                     * Okuma başarısızsa
                                                     * herhangi bir yazı
                                                     * göstermiyoruz.
                                                     */
                                                }
                                            )
                                        }
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
                        containerColor = DarkRed
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
         *
         * Artık:
         * SÜREKLİ TARAMA
         * Okunan: ...
         * ... farklı ürün
         *
         * şeklinde büyük yazılar yok.
         *
         * Bunun yerine küçük bilgi butonları var.
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
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            ScanInfoButton(
                modifier = Modifier.weight(1f),
                title = "OKUNAN",
                value = "${scanBatch.totalQuantity}",
                color = DarkRed
            )

            ScanInfoButton(
                modifier = Modifier.weight(1f),
                title = "FARKLI ÜRÜN",
                value = "${scanBatch.differentProductCount}",
                color = SteelBlue
            )
        }

        /*
         * =========================================================
         * TARAYICI ALANI
         * =========================================================
         *
         * Karenin dışı koyu.
         * Ortası kamera görüntüsü olarak kalıyor.
         *
         * Kullanıcının istediği Trendyol tarzı merkezi
         * barkod tarama alanı.
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.96f),
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
                            color = SuccessGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                pendingProductCode
                                    ?: item.productCode,
                            color = DarkRed,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                "${item.color} • ${item.size}",
                            color = Color.DarkGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        /*
         * =========================================================
         * ALT ONAY / REDDET
         * =========================================================
         *
         * Global alt menünün üzerinde kalacak şekilde
         * biraz yukarıda tutuluyor.
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
                    modifier = Modifier.weight(1f),
                    onClick = {

                        pendingItem = null
                        pendingProductCode = null

                        scanSuccess = false

                        /*
                         * Yeni etiket okunabilsin.
                         */
                        lastDetectedValue = null
                        lastDetectedTime = 0L
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RejectRed
                    )
                ) {

                    Text(
                        text = "REDDET",
                        fontWeight = FontWeight.Bold
                    )
                }

                /*
                 * ONAYLA
                 */
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {

                        val confirmedItem =
                            pendingItem
                                ?: return@Button

                        /*
                         * Ürün artık gerçekten listeye ekleniyor.
                         */
                        scanBatch =
                            scanBatch.addItem(
                                confirmedItem
                            )

                        pendingItem = null
                        pendingProductCode = null

                        scanSuccess = false

                        /*
                         * Aynı ürün tekrar okutulabilsin.
                         */
                        lastDetectedValue = null
                        lastDetectedTime = 0L
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkRed
                    )
                ) {

                    Text(
                        text = "ONAYLA",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        /*
         * =========================================================
         * TARAMAYI BİTİR
         * =========================================================
         *
         * Henüz bekleyen ürün yoksa küçük bir bitirme
         * butonu gösteriyoruz.
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        Color.Black.copy(alpha = 0.65f)
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
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                    Color.Black.copy(alpha = 0.55f)
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
    }
}

/**
 * Küçük OKUNAN / FARKLI ÜRÜN bilgi butonu.
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
            .background(
                Color.Black.copy(alpha = 0.58f),
                RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.8f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
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
                modifier = Modifier.width(6.dp)
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
 * Trendyol benzeri merkezi tarama alanı.
 *
 * Ortadaki kare açık kalır.
 * Karenin dışındaki alanlar karartılır.
 */
@Composable
private fun ScanAreaOverlay(
    success: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            /*
             * Üst karartma.
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DarkOverlay)
            )

            /*
             * Orta bölüm.
             */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {

                /*
                 * Sol karartma.
                 */
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(DarkOverlay)
                )

                /*
                 * ŞEFFAF TARAMA KARESİ.
                 */
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .border(
                            width = if (success) {
                                4.dp
                            } else {
                                2.dp
                            },
                            color = if (success) {
                                SuccessGreen
                            } else {
                                Color.White
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                )

                /*
                 * Sağ karartma.
                 */
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(DarkOverlay)
                )
            }

            /*
             * Alt karartma.
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DarkOverlay)
            )
        }

        /*
         * Köşe işaretleri.
         */
        ScanCorners(
            success = success
        )
    }
}

/**
 * Tarama karesinin dört köşesi.
 */
@Composable
private fun ScanCorners(
    success: Boolean
) {
    val cornerColor =
        if (success) {
            SuccessGreen
        } else {
            Color.White
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        /*
         * Sol üst
         */
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(280.dp)
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(34.dp)
                    .border(
                        width = 4.dp,
                        color = cornerColor,
                        shape = RoundedCornerShape(
                            topStart = 8.dp
                        )
                    )
            )

            /*
             * Sağ üst
             */
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(34.dp)
                    .border(
                        width = 4.dp,
                        color = cornerColor,
                        shape = RoundedCornerShape(
                            topEnd = 8.dp
                        )
                    )
            )

            /*
             * Sol alt
             */
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(34.dp)
                    .border(
                        width = 4.dp,
                        color = cornerColor,
                        shape = RoundedCornerShape(
                            bottomStart = 8.dp
                        )
                    )
            )

            /*
             * Sağ alt
             */
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(34.dp)
                    .border(
                        width = 4.dp,
                        color = cornerColor,
                        shape = RoundedCornerShape(
                            bottomEnd = 8.dp
                        )
                    )
            )
        }
    }
}

/**
 * Kameradan gelen barkod/OCR metnini ürün bilgisine
 * çevirmeye çalışır.
 *
 * Desteklenen:
 *
 * SNZ-2926
 * SNZ 2926
 * MTS-2949
 * FS-1393
 */
private fun processDetectedText(
    rawText: String,
    currentBatch: CameraScanBatch,
    lastValue: String?,
    lastTime: Long,
    onAccepted: (CameraScanItem, String) -> Unit,
    onRejected: () -> Unit
) {

    val normalizedText =
        rawText
            .uppercase(Locale("tr", "TR"))
            .replace("\n", " ")

    val productRegex =
        Regex(
            pattern = "(SNZ|MTS|FS)[\\s-]?(\\d{3,5})"
        )

    val match =
        productRegex.find(normalizedText)

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

    /*
     * Aynı etiketin kameranın önünde kalması durumunda
     * tekrar tekrar okutulmasını önle.
     */
    val now =
        System.currentTimeMillis()

    if (
        lastValue == productCode &&
        now - lastTime < 1500L
    ) {
        return
    }

    val color =
        extractColor(normalizedText)

    val size =
        extractSize(normalizedText)

    /*
     * Sistem barkodu:
     *
     * SNZ-2926 -> 00002926
     */
    val numericPart =
        number
            .takeLast(8)
            .padStart(8, '0')

    val item =
        CameraScanItem(
            productCode = productCode,
            systemBarcode = numericPart,
            color = color,
            size = size
        )

    onAccepted(
        item,
        productCode
    )
}

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