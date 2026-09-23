package com.example.depotakipai.data.repository

import android.content.Context

import com.example.depotakipai.data.local.DatabaseProvider
import com.example.depotakipai.domain.repository.StockMovementRepository

object StockMovementRepositoryProvider {

    fun getRepository(
        context: Context
    ): StockMovementRepository {

        val database = DatabaseProvider.getDatabase(
            context
        )

        return StockMovementRepositoryImpl(
            stockMovementDao = database.stockMovementDao()
        )
    }
}