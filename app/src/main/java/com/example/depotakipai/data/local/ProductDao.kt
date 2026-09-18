package com.example.depotakipai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.depotakipai.data.model.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE productCode = :productCode LIMIT 1")
    suspend fun getProductByCode(
        productCode: String
    ): ProductEntity?

    @Query("SELECT * FROM products WHERE systemBarcode = :systemBarcode LIMIT 1")
    suspend fun getProductByBarcode(
        systemBarcode: String
    ): ProductEntity?

    @Insert
    suspend fun insertProduct(
        product: ProductEntity
    )

    @Update
    suspend fun updateProduct(
        product: ProductEntity
    )

    @Delete
    suspend fun deleteProduct(
        product: ProductEntity
    )
}