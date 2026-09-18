package com.example.depotakipai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.depotakipai.data.model.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
}