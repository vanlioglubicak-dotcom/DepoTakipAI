package com.example.depotakipai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "catalog_items"
)
data class CatalogItemEntity(

    @PrimaryKey
    val id: String,

    val productNumber: String,

    val company: String,

    val imagePath: String,

    val catalogPage: Int = 0,

    val color: String = "",

    val size: String = "",

    val createdAt: Long
)