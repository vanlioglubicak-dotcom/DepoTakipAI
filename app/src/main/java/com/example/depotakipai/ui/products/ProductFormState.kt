package com.example.depotakipai.ui.products

data class ProductFormState(
    val productCode: String = "",
    val systemBarcode: String = "",
    val color: String = "",
    val size: String = "",
    val quantity: String = "0",
    val rowNumber: String = "",
    val shelfNumber: String = "",
    val position: String = ""
)