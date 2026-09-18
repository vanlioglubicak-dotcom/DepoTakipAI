package com.example.depotakipai.domain.repository

import com.example.depotakipai.domain.model.Product

interface ProductRepository {

    fun getAllProducts(): List<Product>

    fun getProductByCode(
        productCode: String
    ): Product?

    fun getProductByBarcode(
        systemBarcode: String
    ): Product?

    fun addProduct(
        product: Product
    )

    fun updateProduct(
        product: Product
    )

    fun deleteProduct(
        productCode: String
    )
}