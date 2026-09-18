package com.example.depotakipai.data.repository

import com.example.depotakipai.data.local.ProductDao
import com.example.depotakipai.data.model.ProductEntity
import com.example.depotakipai.domain.model.Product
import com.example.depotakipai.domain.model.ShelfPosition
import com.example.depotakipai.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productDao: ProductDao
) : ProductRepository {

    override suspend fun getAllProducts(): List<Product> {
        return productDao
            .getAllProducts()
            .map { it.toDomain() }
    }

    override suspend fun getProductByCode(
        productCode: String
    ): Product? {
        return productDao
            .getProductByCode(productCode)
            ?.toDomain()
    }

    override suspend fun getProductByBarcode(
        systemBarcode: String
    ): Product? {
        return productDao
            .getProductByBarcode(systemBarcode)
            ?.toDomain()
    }

    override suspend fun addProduct(
        product: Product
    ) {
        productDao.insertProduct(
            product.toEntity()
        )
    }

    override suspend fun updateProduct(
        product: Product
    ) {
        productDao.updateProduct(
            product.toEntity()
        )
    }

    override suspend fun deleteProduct(
        productCode: String
    ) {
        productDao
            .getProductByCode(productCode)
            ?.let {
                productDao.deleteProduct(it)
            }
    }
}

private fun ProductEntity.toDomain(): Product {

    return Product(
        productCode = productCode,
        systemBarcode = systemBarcode,
        color = color,
        size = size,
        quantity = quantity,
        rowNumber = rowNumber,
        shelfNumber = shelfNumber,
        position = position?.let {
            runCatching {
                ShelfPosition.valueOf(it)
            }.getOrNull()
        },
        photoUri = photoUri,
        createdAt = createdAt
    )
}

private fun Product.toEntity(): ProductEntity {

    return ProductEntity(
        productCode = productCode,
        systemBarcode = systemBarcode,
        color = color,
        size = size,
        quantity = quantity,
        rowNumber = rowNumber,
        shelfNumber = shelfNumber,
        position = position?.name,
        photoUri = photoUri,
        createdAt = createdAt
    )
}