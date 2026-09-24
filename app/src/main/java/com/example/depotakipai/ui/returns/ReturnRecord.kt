package com.example.depotakipai.domain.model

data class ReturnRecord(
    val id: Long = 0L,
    val type: ReturnType,
    val productCode: String,
    val systemBarcode: String? = null,
    val color: String,
    val size: String,
    val quantity: Int,
    val source: ReturnSource? = null,
    val destination: ReturnDestination? = null,
    val status: ReturnStatus = ReturnStatus.DRAFT,
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)