package com.example.depotakipai.domain.model

data class InventoryList(
    val id: Long = 0L,
    val type: InventoryListType,
    val createdAt: Long,
    val productCount: Int,
    val totalQuantity: Int,
    val items: List<InventoryListItem> = emptyList(),
    val sourcePhotoUri: String? = null,
    val isApproved: Boolean = false,
    val destination: String? = null
)