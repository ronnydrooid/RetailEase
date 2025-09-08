package com.example.retailease.repository

import com.example.retailease.datasource.dao.OrderDao
import com.example.retailease.models.Order
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OrderRepository @Inject constructor(private val orderDao: OrderDao){

    suspend fun insertOrder(order: Order): Long{
        return orderDao.insertOrder(order)
    }

    suspend fun deleteOrder(order: Order){
        orderDao.deleteOrder(order)
    }

    suspend fun getOrderById(orderId: Int): Order{
        return orderDao.getOrderById(orderId)
    }

    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders()
    }

    fun getLatestOrder(): Flow<Order?> {
        return orderDao.getLatestOrder()
    }
}