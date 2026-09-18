package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Product
import com.example.depotakipai.domain.repository.ProductRepository

class GetProductByBarcodeUseCase(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(
        systemBarcode: String
    ): Product? {
        return productRepository.getProductByBarcode(systemBarcode)
    }
}