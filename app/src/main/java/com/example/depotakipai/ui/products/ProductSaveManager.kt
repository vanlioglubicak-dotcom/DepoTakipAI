package com.example.depotakipai.ui.products

import com.example.depotakipai.domain.repository.ProductRepository

class ProductSaveManager(
    private val productRepository: ProductRepository
) {

    suspend fun save(
        formState: ProductFormState
    ): Boolean {

        val product = formState.toProduct()

        if (product.productCode.isBlank()) {
            return false
        }

        if (product.color.isBlank()) {
            return false
        }

        if (product.size.isBlank()) {
            return false
        }

        if (product.quantity < 0) {
            return false
        }

        val existingProduct =
            productRepository.getProductByCode(
                product.productCode
            )

        if (existingProduct != null) {
            return false
        }

        productRepository.addProduct(product)

        return true
    }
}