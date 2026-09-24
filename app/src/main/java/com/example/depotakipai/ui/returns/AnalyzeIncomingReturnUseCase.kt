package com.example.depotakipai.domain.usecase.returns

import android.graphics.Bitmap
import com.example.depotakipai.data.camera.IncomingReturnBarcodeAnalyzer
import com.example.depotakipai.data.camera.IncomingReturnOcrAnalyzer
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult
import com.example.depotakipai.domain.model.ReturnSource

class AnalyzeIncomingReturnUseCase(
    private val ocrAnalyzer: IncomingReturnOcrAnalyzer =
        IncomingReturnOcrAnalyzer(),
    private val barcodeAnalyzer: IncomingReturnBarcodeAnalyzer =
        IncomingReturnBarcodeAnalyzer()
) {

    suspend operator fun invoke(
        bitmap: Bitmap,
        source: ReturnSource? = null,
        photoUri: String? = null,
        barcode: String? = null
    ): IncomingReturnAnalysisResult {

        var result = ocrAnalyzer.analyze(
            bitmap = bitmap,
            source = source,
            photoUri = photoUri
        )

        if (!barcode.isNullOrBlank()) {
            result = barcodeAnalyzer.analyze(
                barcode = barcode,
                currentResult = result
            )
        }

        return result
    }

    fun close() {
        ocrAnalyzer.close()
    }
}