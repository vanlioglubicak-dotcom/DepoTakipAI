package com.example.depotakipai.ui.warehouse

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WarehouseViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(WarehouseViewModel::class.java)) {
            return WarehouseViewModel(
                application = application
            ) as T
        }

        throw IllegalArgumentException(
            "Bilinmeyen ViewModel sınıfı: ${modelClass.name}"
        )
    }
}