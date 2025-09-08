package com.example.retailease.repository

import com.example.retailease.datasource.dao.SalesmanOrderDao
import com.example.retailease.models.SalesmanOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesmanOrderRepository @Inject constructor(private val salesmanOrderDao: SalesmanOrderDao) {
    suspend fun insertSalesmanOrder(salesmanOrder: SalesmanOrder): Long {
       return salesmanOrderDao.insertSalesmanOrder(salesmanOrder)
    }
    fun getAllSalesmanOrders(): Flow<List<SalesmanOrder>> {
        return salesmanOrderDao.getAllSalesmanOrders()
    }
    suspend fun getSalesmanOrderById(salesmanOrderId: Int): SalesmanOrder {
        return salesmanOrderDao.getSalesmanOrderById(salesmanOrderId)
    }

}