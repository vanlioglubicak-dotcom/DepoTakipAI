package com.example.depotakipai.data.repository

import android.content.Context
import com.example.depotakipai.data.local.DatabaseProvider
import com.example.depotakipai.domain.repository.ProductRepository

object ProductRepositoryProvider {

    fun provide(
        context: Context
    ): ProductRepository {

        val database = DatabaseProvider.getDatabase(context)

        return ProductRepositoryImpl(
            productDao = database.productDao()
        )
    }
}