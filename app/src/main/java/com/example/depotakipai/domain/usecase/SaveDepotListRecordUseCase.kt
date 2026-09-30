package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.repository.DepotListRecordRepository
import com.example.depotakipai.domain.model.DepotListRecord

class SaveDepotListRecordUseCase(
    private val repository: DepotListRecordRepository
) {

    suspend operator fun invoke(
        record: DepotListRecord
    ) {
        repository.insert(record)
    }

    suspend operator fun invoke(
        records: List<DepotListRecord>
    ) {
        repository.insertAll(records)
    }

    suspend fun updateRecords(
        records: List<DepotListRecord>,
        productCode: String,
        color: String,
        size: String,
        quantity: Int
    ) {
        repository.updateRecords(
            records = records,
            productCode = productCode,
            color = color,
            size = size,
            quantity = quantity
        )
    }
}