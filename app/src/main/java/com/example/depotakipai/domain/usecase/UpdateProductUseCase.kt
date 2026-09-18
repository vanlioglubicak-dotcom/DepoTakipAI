package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Product
import com.example.depotakipai.domain.repository.ProductRepository

class UpdateProductUseCase(
    private val productRepository: ProductRepository
) {

    operator fun invoke(product: Product) {
        productRepository.updateProduct(product)
    }
}