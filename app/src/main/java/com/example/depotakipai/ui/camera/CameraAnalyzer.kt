package com.example.depotakipai

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CameraAnalyzer(
    private val onResult: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val barcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                com.google.mlkit.vision.barcode.common.Barcode.FORMAT_ALL_FORMATS
            )
            .build()
    )

    private val textRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        val mediaImage = image.image

        if (mediaImage == null) {
            image.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            image.imageInfo.rotationDegrees
        )

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->

                val barcodeResults = barcodes
                    .mapNotNull { it.rawValue }
                    .filter { it.isNotBlank() }

                if (barcodeResults.isNotEmpty()) {
                    onResult(barcodeResults.joinToString("\n"))
                }
            }
            .addOnFailureListener {
                // Barkod okunamazsa OCR devam edecek.
            }

        textRecognizer.process(inputImage)
            .addOnSuccessListener { result ->

                val text = result.text.trim()

                if (text.isNotEmpty()) {
                    onResult(text)
                }
            }
            .addOnFailureListener {
                // OCR başarısız olursa kamera çalışmaya devam eder.
            }
            .addOnCompleteListener {
                image.close()
            }
    }
}