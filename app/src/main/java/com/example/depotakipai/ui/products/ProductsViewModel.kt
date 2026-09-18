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

    var isLoading by mutableStateOf(false)
        private set

    var saveResult by mutableStateOf<Boolean?>(null)
        private set

    fun loadProducts() {
        viewModelScope.launch {

            isLoading = true

            val result = withContext(Dispatchers.IO) {
                productRepository.getAllProducts()
            }

            products = result
            isLoading = false
        }
    }

    fun addProduct(
        product: Product,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {

            isLoading = true
            saveResult = null

            val result = withContext(Dispatchers.IO) {

                val existingProduct =
                    productRepository.getProductByCode(
                        product.productCode
                    )

                if (existingProduct != null) {
                    false
                } else {
                    productRepository.addProduct(product)
                    true
                }
            }

            saveResult = result

            if (result) {
                loadProducts()
            } else {
                isLoading = false
            }

            onResult(result)
        }
    }

    fun updateProduct(
        product: Product,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {

            isLoading = true

            withContext(Dispatchers.IO) {
                productRepository.updateProduct(product)
            }

            loadProducts()

            onResult(true)
        }
    }

    fun deleteProduct(
        productCode: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {

            isLoading = true

            withContext(Dispatchers.IO) {
                productRepository.deleteProduct(productCode)
            }

            loadProducts()

            onResult(true)
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

    fun clearSaveResult() {
        saveResult = null
    }
}