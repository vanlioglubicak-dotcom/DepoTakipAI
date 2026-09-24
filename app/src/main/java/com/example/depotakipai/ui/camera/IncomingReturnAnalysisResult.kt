package com.example.depotakipai.domain.model

data class IncomingReturnAnalysisResult(
    val productCode: String? = null,
    val systemBarcode: String? = null,
    val color: String? = null,
    val size: String? = null,
    val quantity: Int? = null,
    val source: ReturnSource? = null,
    val photoUri: String? = null,
    val confidence: Float = 0f,
    val rawText: String = "",
    val isValid: Boolean = false,
    val requiresManualReview: Boolean = true
)