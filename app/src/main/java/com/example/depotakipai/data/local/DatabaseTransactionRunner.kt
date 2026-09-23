package com.example.depotakipai.data.local

import androidx.room.withTransaction

class DatabaseTransactionRunner(
    private val database: AppDatabase
) {

    suspend fun <T> run(
        block: suspend () -> T
    ): T {
        return database.withTransaction {
            block()
        }
    }
}