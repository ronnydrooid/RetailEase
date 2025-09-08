package com.example.retailease.repository

import com.example.retailease.datasource.dao.SalesmanOrderItemDao
import com.example.retailease.models.SalesmanOrderItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesmanOrderItemRepository @Inject constructor(private val salesmanOrderItemDao: SalesmanOrderItemDao) {
    suspend fun insertSalesmanOrderItems(salesmanOrderItems: List<SalesmanOrderItem>) {
        salesmanOrderItemDao.insertSalesmanOrderItems(salesmanOrderItems)
    }
    suspend fun getSalesmanOrderItemsByOrderId(salesmanOrderId: Int): List<SalesmanOrderItem> {
        return salesmanOrderItemDao.getSalesmanOrderItemsByOrderId(salesmanOrderId)
    }

}