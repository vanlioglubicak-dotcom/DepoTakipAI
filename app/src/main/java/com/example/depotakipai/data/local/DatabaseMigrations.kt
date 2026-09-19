package com.example.depotakipai.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            CREATE TABLE products_new (
                productCode TEXT NOT NULL,
                systemBarcode TEXT,
                color TEXT NOT NULL,
                size TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                rowNumber INTEGER,
                shelfNumber TEXT,
                position TEXT,
                photoUri TEXT,
                createdAt INTEGER NOT NULL,
                PRIMARY KEY(productCode)
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            INSERT INTO products_new (
                productCode,
                systemBarcode,
                color,
                size,
                quantity,
                rowNumber,
                shelfNumber,
                position,
                photoUri,
                createdAt
            )
            SELECT
                productCode,
                systemBarcode,
                color,
                size,
                quantity,
                rowNumber,
                CAST(shelfNumber AS TEXT),
                position,
                photoUri,
                createdAt
            FROM products
            """.trimIndent()
        )

        database.execSQL(
            "DROP TABLE products"
        )

        database.execSQL(
            "ALTER TABLE products_new RENAME TO products"
        )
    }
}