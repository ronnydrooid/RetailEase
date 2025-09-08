package com.example.retailease.repository

import com.example.retailease.datasource.dao.SalesmanDraftOrderDao
import com.example.retailease.models.SalesmanDraftOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesmanDraftOrderRepository @Inject constructor(private val salesmanDraftOrderDao: SalesmanDraftOrderDao) {
    suspend fun insertSalesmanDraftOrder(salesmanDraftOrder: SalesmanDraftOrder): Long{
        return salesmanDraftOrderDao.insertSalesmanDraftOrder(salesmanDraftOrder)
    }
    suspend fun deleteSalesmanDraftOrder(salesmanDraftOrder: SalesmanDraftOrder){
        salesmanDraftOrderDao.deleteSalesmanDraftOrder(salesmanDraftOrder)
    }
    suspend fun getSalesmanDraftOrderById(salesmanDraftOrderId: Int): SalesmanDraftOrder{
        return salesmanDraftOrderDao.getSalesmanDraftOrderById(salesmanDraftOrderId)
    }
    fun getSalesmanDraftOrders(): Flow<List<SalesmanDraftOrder>> {
        return salesmanDraftOrderDao.getSalesmanDraftOrders()
    }
}