package com.example.depotakipai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "return_records")
data class ReturnRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val type: String,

    val productCode: String,

    val systemBarcode: String? = null,

    val color: String,

    val size: String,

    val quantity: Int,

    val source: String? = null,

    val destination: String? = null,

    val status: String,

    val photoUri: String? = null,

    val createdAt: Long
)