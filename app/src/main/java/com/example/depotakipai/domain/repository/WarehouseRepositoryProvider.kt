package com.example.depotakipai.domain.repository

import android.content.Context
import com.example.depotakipai.data.local.DatabaseProvider
import com.example.depotakipai.data.repository.WarehouseRepositoryImpl

object WarehouseRepositoryProvider {

    fun provide(
        context: Context
    ): WarehouseRepository {

        val database = DatabaseProvider.getDatabase(
            context
        )

        return WarehouseRepositoryImpl(
            warehouseDao = database.warehouseDao()
        )
    }
}