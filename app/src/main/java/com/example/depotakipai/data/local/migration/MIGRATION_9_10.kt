package com.example.depotakipai.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE warehouse_locations " +
                    "ADD COLUMN productCode TEXT NOT NULL DEFAULT ''"
        )
    }
}