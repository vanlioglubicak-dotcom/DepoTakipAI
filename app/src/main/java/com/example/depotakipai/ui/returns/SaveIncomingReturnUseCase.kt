package com.example.depotakipai.domain.usecase.returns

import com.example.depotakipai.data.local.entity.ReturnRecordEntity
import com.example.depotakipai.data.repository.ReturnRepository
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult
import com.example.depotakipai.domain.model.ReturnStatus
import com.example.depotakipai.domain.model.ReturnType

class SaveIncomingReturnUseCase(
    private val returnRepository: ReturnRepository
) {

    suspend operator fun invoke(
        result: IncomingReturnAnalysisResult
    ): Long {

        require(
            !result.productCode.isNullOrBlank()
        ) {
            "Ürün kodu boş olamaz."
        }

        require(
            !result.color.isNullOrBlank()
        ) {
            "Renk boş olamaz."
        }

        require(
            !result.size.isNullOrBlank()
        ) {
            "Beden boş olamaz."
        }

        require(
            (result.quantity ?: 0) > 0
        ) {
            "Adet 0'dan büyük olmalıdır."
        }

        val entity = ReturnRecordEntity(
            type = ReturnType.INCOMING.name,
            productCode = result.productCode,
            systemBarcode = result.systemBarcode,
            color = result.color,
            size = result.size,
            quantity = result.quantity ?: 1,
            source = result.source?.name,
            destination = null,
            status = ReturnStatus.COMPLETED.name,
            photoUri = result.photoUri,
            createdAt = System.currentTimeMillis()
        )

        return returnRepository.insertReturn(
            entity
        )
    }
}