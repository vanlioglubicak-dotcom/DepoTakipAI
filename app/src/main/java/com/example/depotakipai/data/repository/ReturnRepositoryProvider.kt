package com.example.depotakipai.data.repository

import com.example.depotakipai.data.local.AppDatabase

object ReturnRepositoryProvider {

    fun create(
        database: AppDatabase
    ): ReturnRepository {
        return ReturnRepository(
            returnDao = database.returnDao()
        )
    }
}