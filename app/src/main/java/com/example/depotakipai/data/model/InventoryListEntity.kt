package com.example.depotakipai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_lists")
data class InventoryListEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val type: String,

    val createdAt: Long,

    val productCount: Int,

    val totalQuantity: Int,

    val sourcePhotoUri: String?,

    val isApproved: Boolean,

    val destination: String?
)