package com.example.retailease.repository

import com.example.retailease.datasource.dao.SalesmanDraftOrderItemDao
import com.example.retailease.models.SalesmanDraftOrderItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesmanDraftOrderItemRepository @Inject constructor(private val salesmanDraftOrderItemDao: SalesmanDraftOrderItemDao) {
    suspend fun insertSalesmanDraftOrderItems(salesmanDraftOrderItems: List<SalesmanDraftOrderItem>) {
        salesmanDraftOrderItemDao.insertSalesmanDraftOrderItems(salesmanDraftOrderItems)

    }
    suspend fun deleteSalesmanDraftOrderItems(salesmanDraftOrderId: Int) {
        salesmanDraftOrderItemDao.deleteSalesmanDraftOrderItems(salesmanDraftOrderId)
    }
    suspend fun getSalesmanDraftOrderItemsById(salesmanDraftOrderId: Int): List<SalesmanDraftOrderItem> {
        return salesmanDraftOrderItemDao.getSalesmanDraftOrderItemsById(salesmanDraftOrderId)
    }
}