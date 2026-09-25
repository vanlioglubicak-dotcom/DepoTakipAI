package com.example.depotakipai.domain.usecase.returns

import com.example.depotakipai.data.local.entity.ReturnRecordEntity
import com.example.depotakipai.data.repository.ReturnRepository

class GetIncomingReturnsUseCase(
    private val returnRepository: ReturnRepository
) {

    suspend operator fun invoke(): List<ReturnRecordEntity> {
        return returnRepository.getReturnsByType(
            "INCOMING"
        )
    }
}