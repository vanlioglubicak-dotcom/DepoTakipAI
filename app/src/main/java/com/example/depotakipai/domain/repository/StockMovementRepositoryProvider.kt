package com.example.depotakipai.domain.repository

import android.content.Context
import com.example.depotakipai.data.local.DatabaseProvider
import com.example.depotakipai.data.repository.StockMovementRepositoryImpl

object StockMovementRepositoryProvider {

    fun provide(context: Context): StockMovementRepository {
        val database = DatabaseProvider.getDatabase(context)

        return StockMovementRepositoryImpl(
            stockMovementDao = database.stockMovementDao()
        )
    }
}