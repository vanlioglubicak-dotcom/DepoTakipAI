package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Product
import com.example.depotakipai.domain.repository.ProductRepository

class GetProductByCodeUseCase(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(
        productCode: String
    ): Product? {
        return productRepository.getProductByCode(productCode)
    }
}