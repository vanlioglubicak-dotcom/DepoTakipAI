package com.example.depotakipai.data.repository

import android.content.Context
import com.example.depotakipai.data.local.DatabaseProvider
import com.example.depotakipai.domain.repository.StockRepository

object StockRepositoryProvider {

    fun getRepository(
        context: Context
    ): StockRepository {

        val database = DatabaseProvider.getDatabase(context)

        return StockRepositoryImpl(
            stockDao = database.stockDao()
        )
    }
}