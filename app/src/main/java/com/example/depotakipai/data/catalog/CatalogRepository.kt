package com.example.depotakipai.data.catalog

import com.example.depotakipai.data.local.dao.CatalogItemDao
import com.example.depotakipai.data.local.entity.CatalogItemEntity
import kotlinx.coroutines.flow.Flow

class CatalogRepository(
    private val catalogItemDao: CatalogItemDao
) {

    fun getAll(): Flow<List<CatalogItemEntity>> {
        return catalogItemDao.getAll()
    }

    fun getByProductNumber(
        productNumber: String
    ): Flow<List<CatalogItemEntity>> {
        return catalogItemDao.getByProductNumber(
            productNumber
        )
    }

    fun getByCompany(
        company: String
    ): Flow<List<CatalogItemEntity>> {
        return catalogItemDao.getByCompany(
            company
        )
    }

    suspend fun getById(
        id: String
    ): CatalogItemEntity? {
        return catalogItemDao.getById(
            id
        )
    }

    suspend fun insert(
        item: CatalogItemEntity
    ) {
        catalogItemDao.insert(
            item
        )
    }

    suspend fun insertAll(
        items: List<CatalogItemEntity>
    ) {
        catalogItemDao.insertAll(
            items
        )
    }

    suspend fun delete(
        item: CatalogItemEntity
    ) {
        catalogItemDao.delete(
            item
        )
    }

    suspend fun deleteById(
        id: String
    ) {
        catalogItemDao.deleteById(
            id
        )
    }

    suspend fun deleteAll() {
        catalogItemDao.deleteAll()
    }
}