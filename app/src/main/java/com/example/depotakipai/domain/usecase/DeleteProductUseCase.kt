package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.repository.ProductRepository

class DeleteProductUseCase(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(
        productCode: String
    ) {
        productRepository.deleteProduct(productCode)
    }
}