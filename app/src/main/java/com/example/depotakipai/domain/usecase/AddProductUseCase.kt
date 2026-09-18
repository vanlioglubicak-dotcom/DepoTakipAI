package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.Product
import com.example.depotakipai.domain.repository.ProductRepository

class AddProductUseCase(
    private val productRepository: ProductRepository
) {

    operator fun invoke(product: Product) {
        productRepository.addProduct(product)
    }
}