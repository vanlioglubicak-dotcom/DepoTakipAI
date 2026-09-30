package com.example.depotakipai.data.repository

import com.example.depotakipai.data.local.dao.DepotListRecordDao
import com.example.depotakipai.data.local.mapper.toDomain
import com.example.depotakipai.data.local.mapper.toEntity
import com.example.depotakipai.domain.model.DepotListCategory
import com.example.depotakipai.domain.model.DepotListRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DepotListRecordRepository(
    private val dao: DepotListRecordDao
) {

    fun observeAll(): Flow<List<DepotListRecord>> {
        return dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun observeByCategory(
        category: DepotListCategory
    ): Flow<List<DepotListRecord>> {
        return dao.getByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun insert(
        record: DepotListRecord
    ) {
        dao.insert(record.toEntity())
    }

    suspend fun insertAll(
        records: List<DepotListRecord>
    ) {
        dao.insertAll(
            records.map { it.toEntity() }
        )
    }

    suspend fun delete(
        record: DepotListRecord
    ) {
        dao.delete(record.toEntity())
    }

    suspend fun deleteById(
        id: String
    ) {
        dao.deleteById(id)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun updateRecords(
        records: List<DepotListRecord>,
        productCode: String,
        color: String,
        size: String,
        quantity: Int
    ) {
        if (records.isEmpty() || quantity <= 0) {
            return
        }

        val template = records.first()

        records.forEach { record ->
            dao.deleteById(record.id)
        }

        val updatedRecords =
            List(quantity) {
                DepotListRecord(
                    category = template.category,
                    productCode = productCode.trim(),
                    color = color.trim(),
                    size = size.trim(),
                    quantity = 1,
                    createdAt = System.currentTimeMillis()
                )
            }

        dao.insertAll(
            updatedRecords.map {
                it.toEntity()
            }
        )
    }
}