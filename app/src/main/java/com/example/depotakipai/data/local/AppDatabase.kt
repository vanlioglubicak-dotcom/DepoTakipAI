package com.example.depotakipai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.depotakipai.data.local.dao.ReturnDao
import com.example.depotakipai.data.local.entity.ReturnRecordEntity
import com.example.depotakipai.data.model.InventoryListEntity
import com.example.depotakipai.data.model.InventoryListItemEntity
import com.example.depotakipai.data.model.ProductEntity
import com.example.depotakipai.data.model.StockEntity
import com.example.depotakipai.data.model.StockMovementEntity
import com.example.depotakipai.data.model.WarehouseLocationEntity
import com.example.depotakipai.data.model.WarehousePhotoEntity
import com.example.depotakipai.data.model.WarehouseRackEntity
import com.example.depotakipai.data.model.WarehouseRowEntity

@Database(
    entities = [
        ProductEntity::class,
        InventoryListEntity::class,
        InventoryListItemEntity::class,
        StockEntity::class,
        StockMovementEntity::class,
        WarehouseRowEntity::class,
        WarehouseRackEntity::class,
        WarehouseLocationEntity::class,
        WarehousePhotoEntity::class,
        ReturnRecordEntity::class
    ],
    version = 7,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun inventoryListDao(): InventoryListDao

    abstract fun stockDao(): StockDao

    abstract fun stockMovementDao(): StockMovementDao

    abstract fun warehouseDao(): WarehouseDao

    abstract fun returnDao(): ReturnDao
}