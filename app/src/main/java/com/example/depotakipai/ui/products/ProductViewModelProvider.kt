package com.example.depotakipai.ui.products

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.depotakipai.data.repository.ProductRepositoryProvider

class ProductViewModelProvider(
    context: Context
) : ViewModelProvider.Factory {

    private val productRepository =
        ProductRepositoryProvider.provide(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {

            return ProductsViewModel(
                productRepository = productRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: ${modelClass.name}"
        )
    }
}