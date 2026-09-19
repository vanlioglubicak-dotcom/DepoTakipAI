package com.example.depotakipai.ui.camera

data class CameraScanResult(
    val productCode: String? = null,
    val systemBarcode: String? = null,
    val color: String? = null,
    val size: String? = null,
    val rawText: String = "",
    val hasValidSystemBarcode: Boolean = false
)