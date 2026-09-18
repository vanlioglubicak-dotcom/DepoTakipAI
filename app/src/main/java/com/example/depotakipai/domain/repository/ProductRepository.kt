package com.example.depotakipai.domain.repository

import com.example.depotakipai.domain.model.Product

interface ProductRepository {

    suspend fun getAllProducts(): List<Product>

    suspend fun getProductByCode(
        productCode: String
    ): Product?

    suspend fun getProductByBarcode(
        systemBarcode: String
    ): Product?

    suspend fun addProduct(
        product: Product
    )

    suspend fun updateProduct(
        product: Product
    )

    suspend fun deleteProduct(
        productCode: String
    )
}