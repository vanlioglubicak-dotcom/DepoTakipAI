package com.example.depotakipai.ui.products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depotakipai.domain.model.Product
import com.example.depotakipai.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    var products by mutableStateOf<List<Product>>(emptyList())
        private set

    fun loadProducts() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                productRepository.getAllProducts()
            }

            products = result
        }
    }

    fun addProduct(product: Product): Boolean {
        var saved = false

        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val existingProduct =
                    productRepository.getProductByCode(
                        product.productCode
                    )

                if (existingProduct == null) {
                    productRepository.addProduct(product)
                    saved = true
                }
            }

            if (saved) {
                loadProducts()
            }
        }

        return saved
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                productRepository.updateProduct(product)
            }

            loadProducts()
        }
    }

    fun deleteProduct(productCode: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                productRepository.deleteProduct(productCode)
            }

            loadProducts()
        }
    }

    fun findByCode(
        productCode: String,
        onResult: (Product?) -> Unit
    ) {
        viewModelScope.launch {
            val product = withContext(Dispatchers.IO) {
                productRepository.getProductByCode(productCode)
            }

            onResult(product)
        }
    }

    fun findByBarcode(
        systemBarcode: String,
        onResult: (Product?) -> Unit
    ) {
        viewModelScope.launch {
            val product = withContext(Dispatchers.IO) {
                productRepository.getProductByBarcode(systemBarcode)
            }

            onResult(product)
        }
    }
}