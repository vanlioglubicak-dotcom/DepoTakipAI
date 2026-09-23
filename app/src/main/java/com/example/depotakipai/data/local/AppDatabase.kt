package com.example.depotakipai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.depotakipai.data.model.InventoryListEntity
import com.example.depotakipai.data.model.InventoryListItemEntity
import com.example.depotakipai.data.model.ProductEntity
import com.example.depotakipai.data.model.StockEntity

@Database(
    entities = [
        ProductEntity::class,
        InventoryListEntity::class,
        InventoryListItemEntity::class,
        StockEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun inventoryListDao(): InventoryListDao

    abstract fun stockDao(): StockDao
}