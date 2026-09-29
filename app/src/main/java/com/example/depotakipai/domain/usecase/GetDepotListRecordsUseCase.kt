package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.repository.DepotListRecordRepository
import com.example.depotakipai.domain.model.DepotListCategory
import com.example.depotakipai.domain.model.DepotListRecord
import kotlinx.coroutines.flow.Flow

class GetDepotListRecordsUseCase(
    private val repository: DepotListRecordRepository
) {

    operator fun invoke(): Flow<List<DepotListRecord>> {
        return repository.observeAll()
    }

    operator fun invoke(
        category: DepotListCategory
    ): Flow<List<DepotListRecord>> {
        return repository.observeByCategory(category)
    }
}