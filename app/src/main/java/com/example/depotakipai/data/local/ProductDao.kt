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
    fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE productCode = :productCode LIMIT 1")
    fun getProductByCode(productCode: String): ProductEntity?

    @Query("SELECT * FROM products WHERE systemBarcode = :systemBarcode LIMIT 1")
    fun getProductByBarcode(systemBarcode: String): ProductEntity?

    @Insert
    fun insertProduct(product: ProductEntity)

    @Update
    fun updateProduct(product: ProductEntity)

    @Delete
    fun deleteProduct(product: ProductEntity)
}