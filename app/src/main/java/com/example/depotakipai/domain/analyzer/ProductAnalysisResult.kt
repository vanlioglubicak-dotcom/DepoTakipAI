package com.example.depotakipai.domain.analyzer

data class ProductAnalysisResult(
    val productCode: String = "",
    val systemBarcode: String = "",
    val size: String = "",
    val color: String = "",
    val quantity: Int = 0,
    val rawText: String = "",
    val confidence: Float = 0f
)