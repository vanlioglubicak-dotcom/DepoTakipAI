package com.example.depotakipai.data.local.mapper

import com.example.depotakipai.data.local.entity.DepotListRecordEntity
import com.example.depotakipai.domain.model.DepotListCategory
import com.example.depotakipai.domain.model.DepotListRecord

fun DepotListRecord.toEntity(): DepotListRecordEntity {
    return DepotListRecordEntity(
        id = id,
        category = category.name,
        productCode = productCode,
        systemBarcode = null,
        color = color,
        size = size,
        quantity = quantity,
        createdAt = createdAt
    )
}

fun DepotListRecordEntity.toDomain(): DepotListRecord {
    return DepotListRecord(
        id = id,
        category = DepotListCategory.valueOf(category),
        productCode = productCode,
        color = color,
        size = size,
        quantity = quantity,
        createdAt = createdAt
    )
}