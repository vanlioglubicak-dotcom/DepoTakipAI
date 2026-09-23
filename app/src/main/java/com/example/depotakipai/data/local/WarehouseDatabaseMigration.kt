package com.example.depotakipai.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_5_6 = object : Migration(5, 6) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {

        // -----------------------------------------------------
        // DEPO SIRALARI
        // -----------------------------------------------------

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS warehouse_rows (
                id INTEGER NOT NULL,
                rowNumber INTEGER NOT NULL,
                name TEXT NOT NULL,
                displayOrder INTEGER NOT NULL,
                isActive INTEGER NOT NULL,
                createdAt INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )

        // -----------------------------------------------------
        // DEPO RAFLARI
        // -----------------------------------------------------

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS warehouse_racks (
                id INTEGER NOT NULL,
                rowId INTEGER NOT NULL,
                rackCode TEXT NOT NULL,
                name TEXT NOT NULL,
                displayOrder INTEGER NOT NULL,
                shelfCount INTEGER NOT NULL,
                isActive INTEGER NOT NULL,
                createdAt INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )

        // -----------------------------------------------------
        // DEPO KONUMLARI
        // -----------------------------------------------------

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS warehouse_locations (
                id INTEGER NOT NULL,
                rackId INTEGER NOT NULL,
                shelfNumber INTEGER NOT NULL,
                locationNumber INTEGER NOT NULL,
                position TEXT NOT NULL,
                locationCode TEXT NOT NULL,
                isActive INTEGER NOT NULL,
                createdAt INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )

        // -----------------------------------------------------
        // DEPO FOTOĞRAFLARI
        // -----------------------------------------------------

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS warehouse_photos (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                rackId INTEGER,
                locationId INTEGER,
                photoUri TEXT NOT NULL,
                title TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // -----------------------------------------------------
        // İNDEKSLER
        // -----------------------------------------------------

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_warehouse_racks_rowId
            ON warehouse_racks(rowId)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_warehouse_locations_rackId
            ON warehouse_locations(rackId)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_warehouse_locations_locationCode
            ON warehouse_locations(locationCode)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_warehouse_photos_rackId
            ON warehouse_photos(rackId)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_warehouse_photos_locationId
            ON warehouse_photos(locationId)
            """.trimIndent()
        )
    }
}