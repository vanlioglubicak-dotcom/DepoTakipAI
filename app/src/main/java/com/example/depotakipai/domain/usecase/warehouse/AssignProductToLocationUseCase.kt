package com.example.depotakipai.domain.usecase.warehouse

import com.example.depotakipai.domain.model.Product
import com.example.depotakipai.domain.model.ShelfPosition
import com.example.depotakipai.domain.repository.ProductRepository
import com.example.depotakipai.domain.repository.WarehouseRepository

class AssignProductToLocationUseCase(
    private val productRepository: ProductRepository,
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(
        productCode: String,
        rowNumber: Int,
        shelfNumber: Int,
        position: ShelfPosition
    ): Result<Product> {

        val product = productRepository.getProductByCode(
            productCode.trim()
        ) ?: return Result.failure(
            IllegalArgumentException(
                "Ürün bulunamadı: $productCode"
            )
        )

        val row = warehouseRepository.getAllRows()
            .firstOrNull { it.rowNumber == rowNumber }
            ?: return Result.failure(
                IllegalArgumentException(
                    "Depo sırası bulunamadı: $rowNumber"
                )
            )

        val rack = warehouseRepository.getRacksByRow(row.id)
            .firstOrNull { it.shelfCount >= shelfNumber }
            ?: return Result.failure(
                IllegalArgumentException(
                    "Belirtilen raf veya kat bulunamadı."
                )
            )

        val locations = warehouseRepository.getLocationsByRack(rack.id)

        val location = locations.firstOrNull {
            it.shelfNumber == shelfNumber &&
                    it.position == position
        } ?: return Result.failure(
            IllegalArgumentException(
                "Belirtilen konum bulunamadı."
            )
        )

        val updatedProduct = product.copy(
            rowNumber = rowNumber,
            shelfNumber = location.locationCode,
            position = position
        )

        productRepository.updateProduct(updatedProduct)

        return Result.success(updatedProduct)
    }
}