package com.example.depotakipai.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncomingReturnCameraScreen(
    onBack: () -> Unit = {},
    onPhotoCaptured: (String) -> Unit = {}
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

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    var isCapturing by remember {
        mutableStateOf(false)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
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

                    cameraProviderFuture.addListener(
                        {
                            val cameraProvider =
                                cameraProviderFuture.get()

                            val preview =
                                androidx.camera.core.Preview
                                    .Builder()
                                    .build()
                                    .also {
                                        it.surfaceProvider =
                                            previewView.surfaceProvider
                                    }

                            val capture =
                                ImageCapture.Builder()
                                    .setCaptureMode(
                                        ImageCapture
                                            .CAPTURE_MODE_MINIMIZE_LATENCY
                                    )
                                    .build()

                            imageCapture = capture

                            cameraProvider.unbindAll()

                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                capture
                            )
                        },
                        ContextCompat.getMainExecutor(
                            viewContext
                        )
                    )

                    previewView
                }
            )

            CameraGuideOverlay()

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 18.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Gelen İade Kamera",
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.size(6.dp)
                )

                Text(
                    text = "İade etiketini çerçeve içine alın",
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 18.dp
                    ),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                OutlinedButton(
                    onClick = onBack,
                    enabled = !isCapturing
                ) {
                    Text(
                        text = "GERİ",
                        color = Color.White
                    )
                }

                Button(
                    onClick = {

                        val capture =
                            imageCapture
                                ?: return@Button

                        if (isCapturing) {
                            return@Button
                        }

                        isCapturing = true

                        val fileName =
                            "gelen_iade_" +
                                    SimpleDateFormat(
                                        "yyyyMMdd_HHmmss",
                                        Locale.US
                                    ).format(Date()) +
                                    ".jpg"

                        val contentValues =
                            ContentValues().apply {
                                put(
                                    MediaStore.Images.Media.DISPLAY_NAME,
                                    fileName
                                )

                                put(
                                    MediaStore.Images.Media.MIME_TYPE,
                                    "image/jpeg"
                                )

                                put(
                                    MediaStore.Images.Media.RELATIVE_PATH,
                                    "Pictures/DepoTakipAI/GelenIadeler"
                                )
                            }

                        val outputOptions =
                            ImageCapture.OutputFileOptions
                                .Builder(
                                    context.contentResolver,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    contentValues
                                )
                                .build()

                        capture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(
                                context
                            ),
                            object :
                                ImageCapture.OnImageSavedCallback {

                                override fun onImageSaved(
                                    outputFileResults:
                                    ImageCapture
                                    .OutputFileResults
                                ) {
                                    isCapturing = false

                                    val savedUri =
                                        outputFileResults.savedUri

                                    if (
                                        savedUri != null
                                    ) {
                                        onPhotoCaptured(
                                            savedUri.toString()
                                        )
                                    }
                                }

                                override fun onError(
                                    exception:
                                    ImageCaptureException
                                ) {
                                    isCapturing = false
                                }
                            }
                        )
                    },
                    enabled = !isCapturing,
                    modifier = Modifier.size(68.dp),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (isCapturing) {
                            "..."
                        } else {
                            "ÇEK"
                        }
                    )
                }
            }

        } else {

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
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.size(12.dp)
                )

                Button(
                    onClick = {
                        permissionLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }
                ) {
                    Text(
                        text = "KAMERA İZNİ VER"
                    )
                }

                Spacer(
                    modifier = Modifier.size(12.dp)
                )

                OutlinedButton(
                    onClick = onBack
                ) {
                    Text(
                        text = "GERİ"
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraGuideOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 32.dp,
                end = 32.dp,
                top = 170.dp,
                bottom = 150.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(18.dp)
                )
                .background(
                    Color.Transparent
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "ETİKETİ BURAYA GETİRİN",
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text(
                    text = "Ürün kodu • beden • renk",
                    color = Color.White
                )
            }
        }
    }
}