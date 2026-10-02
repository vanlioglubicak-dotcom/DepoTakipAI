package com.example.depotakipai.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_8_9 = object : Migration(8, 9) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS catalog_items (
                id TEXT NOT NULL,
                productNumber TEXT NOT NULL,
                company TEXT NOT NULL,
                imagePath TEXT NOT NULL,
                catalogPage INTEGER NOT NULL,
                color TEXT NOT NULL,
                size TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
    }
}