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
                productCode TEXT NOT NULL PRIMARY KEY,
                systemBarcode TEXT,
                color TEXT NOT NULL,
                size TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                rowNumber INTEGER,
                shelfNumber TEXT,
                position TEXT,
                photoUri TEXT,
                createdAt INTEGER NOT NULL
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

val MIGRATION_2_3 = object : Migration(2, 3) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS inventory_lists (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                type TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                productCount INTEGER NOT NULL,
                totalQuantity INTEGER NOT NULL,
                sourcePhotoUri TEXT,
                isApproved INTEGER NOT NULL,
                destination TEXT
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS inventory_list_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                listId INTEGER NOT NULL,
                productCode TEXT NOT NULL,
                systemBarcode TEXT,
                color TEXT NOT NULL,
                size TEXT NOT NULL,
                quantity INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS stocks (
                productCode TEXT NOT NULL PRIMARY KEY,
                systemBarcode TEXT,
                color TEXT NOT NULL,
                size TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                rowNumber INTEGER,
                shelfNumber TEXT,
                position TEXT,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS stock_movements (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                listId INTEGER NOT NULL,
                productCode TEXT NOT NULL,
                color TEXT NOT NULL,
                size TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                direction TEXT NOT NULL,
                destination TEXT,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}