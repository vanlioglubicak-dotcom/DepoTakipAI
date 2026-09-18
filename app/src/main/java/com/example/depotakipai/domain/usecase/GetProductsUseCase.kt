package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Product
import com.example.depotakipai.domain.repository.ProductRepository

class GetProductsUseCase(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(): List<Product> {
        return productRepository.getAllProducts()
    }
}