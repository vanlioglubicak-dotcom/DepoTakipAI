package com.example.depotakipai.data.local

import android.content.Context
import androidx.room.Room
import com.example.depotakipai.data.local.migration.MIGRATION_6_7
import com.example.depotakipai.data.local.migration.MIGRATION_7_8
import com.example.depotakipai.data.local.migration.MIGRATION_8_9

object DatabaseProvider {

    @Volatile
    private var database: AppDatabase? = null

    fun getDatabase(
        context: Context
    ): AppDatabase {

        return database ?: synchronized(this) {

            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "depo_takip_ai.db"
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_7_8,
                    MIGRATION_8_9
                )
                .build()
                .also {
                    database = it
                }
        }
    }
}