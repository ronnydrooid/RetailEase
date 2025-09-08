package com.example.retailease.repository

import com.example.retailease.datasource.dao.DraftOrderDao
import com.example.retailease.models.DraftOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DraftOrderRepository @Inject constructor(private val draftOrderDao: DraftOrderDao) {

    suspend fun insertDraftOrder(draftOrder: DraftOrder): Long {
        return draftOrderDao.insertDraftOrder(draftOrder)

    }

    suspend fun deleteDraftOrder(draftOrder: DraftOrder) {
        draftOrderDao.deleteDraftOrder(draftOrder)
    }

    suspend fun getDraftOrderById(draftOrderId: Int): DraftOrder {
        return draftOrderDao.getDraftOrderById(draftOrderId)
    }

    fun getDraftOrders(): Flow<List<DraftOrder>> {
        return draftOrderDao.getDraftOrders()
    }

}