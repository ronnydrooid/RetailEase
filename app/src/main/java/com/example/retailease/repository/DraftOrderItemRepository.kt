package com.example.retailease.repository

import com.example.retailease.datasource.dao.DraftOrderItemDao
import com.example.retailease.models.DraftOrderItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DraftOrderItemRepository @Inject constructor(private val draftOrderItemDao: DraftOrderItemDao) {

    suspend fun insertDraftOrderItem(draftOrderItem: DraftOrderItem) {
        draftOrderItemDao.insertDraftOrderItem(draftOrderItem)
    }

    suspend fun deleteDraftOrderItemsById(draftOrderId: Int) {
        draftOrderItemDao.deleteDraftOrderItems(draftOrderId)
    }

    suspend fun getDraftOrderItemsById(draftOrderId: Int): List<DraftOrderItem> {
        return draftOrderItemDao.getDraftOrderItemsById(draftOrderId)
    }
}