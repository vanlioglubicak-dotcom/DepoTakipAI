package com.example.depotakipai.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_7 = object : Migration(6, 7) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS return_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                type TEXT NOT NULL,
                productCode TEXT NOT NULL,
                systemBarcode TEXT,
                color TEXT NOT NULL,
                size TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                source TEXT,
                destination TEXT,
                status TEXT NOT NULL,
                photoUri TEXT,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}