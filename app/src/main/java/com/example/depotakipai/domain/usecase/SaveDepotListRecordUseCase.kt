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
}