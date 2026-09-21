package com.example.depotakipai.ui.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeCaptureAnalyzer(
    private val onBarcodeDetected: () -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner =
        BarcodeScanning.getClient()

    private val alreadyDetected =
        AtomicBoolean(false)

    override fun analyze(
        imageProxy: ImageProxy
    ) {

        /*
         * Aynı barkodun arka arkaya tetiklenmesini önle.
         */
        if (alreadyDetected.get()) {

            imageProxy.close()

            return
        }

        val mediaImage =
            imageProxy.image

        if (mediaImage == null) {

            imageProxy.close()

            return
        }

        val image =
            InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

        scanner.process(image)

            .addOnSuccessListener { barcodes ->

                /*
                 * Burada barkodun içeriğinin ne olduğu
                 * önemli değil.
                 *
                 * 85 ile başlayan üretici barkodu da olsa
                 * barkod algılandığı anda fotoğraf çekilecek.
                 *
                 * Çünkü 85... barkodunu daha sonra
                 * sistem barkodu olarak zaten kullanmayacağız.
                 */

                if (
                    barcodes.isNotEmpty() &&
                    alreadyDetected.compareAndSet(
                        false,
                        true
                    )
                ) {

                    onBarcodeDetected()
                }
            }

            .addOnFailureListener {

                /*
                 * Hata olursa kamera çalışmaya devam eder.
                 */
            }

            .addOnCompleteListener {

                imageProxy.close()
            }
    }

    fun reset() {

        alreadyDetected.set(false)
    }
}