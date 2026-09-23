package com.example.depotakipai.data.local

import android.content.Context

object DatabaseTransactionRunnerProvider {

    fun getRunner(
        context: Context
    ): DatabaseTransactionRunner {

        return DatabaseTransactionRunner(
            database = DatabaseProvider.getDatabase(
                context
            )
        )
    }
}