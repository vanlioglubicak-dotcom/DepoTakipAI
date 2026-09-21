package com.example.depotakipai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_list_items")
data class InventoryListItemEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val listId: Long,

    val productCode: String,

    val systemBarcode: String? = null,

    val color: String,

    val size: String,

    val quantity: Int
)