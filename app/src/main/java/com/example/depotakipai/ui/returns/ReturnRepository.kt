package com.example.depotakipai.data.repository

import com.example.depotakipai.data.local.dao.ReturnDao
import com.example.depotakipai.data.local.entity.ReturnRecordEntity

class ReturnRepository(
    private val returnDao: ReturnDao
) {

    suspend fun insertReturn(
        returnRecord: ReturnRecordEntity
    ): Long {
        return returnDao.insertReturn(
            returnRecord
        )
    }

    suspend fun updateReturn(
        returnRecord: ReturnRecordEntity
    ) {
        returnDao.updateReturn(
            returnRecord
        )
    }

    suspend fun deleteReturn(
        returnRecord: ReturnRecordEntity
    ) {
        returnDao.deleteReturn(
            returnRecord
        )
    }

    suspend fun getAllReturns(): List<ReturnRecordEntity> {
        return returnDao.getAllReturns()
    }

    suspend fun getReturnById(
        id: Long
    ): ReturnRecordEntity? {
        return returnDao.getReturnById(
            id
        )
    }

    suspend fun getReturnsByType(
        type: String
    ): List<ReturnRecordEntity> {
        return returnDao.getReturnsByType(
            type
        )
    }

    suspend fun deleteAllReturns() {
        returnDao.deleteAllReturns()
    }
}