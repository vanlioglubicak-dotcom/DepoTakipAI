package com.example.depotakipai.ui.catalog.model

import com.example.depotakipai.data.local.entity.CatalogItemEntity

data class CatalogModelGroup(
    val modelNumber: String,
    val displayModel: String,
    val imagePath: String,
    val colors: List<String>,
    val sizes: List<String>,
    val items: List<CatalogItemEntity>
)