package com.example.retailease.repository

import com.example.retailease.datasource.dao.OrderItemDao
import com.example.retailease.models.OrderItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderItemRepository @Inject constructor(private val orderItemDao: OrderItemDao){
    suspend fun insertOrderItem(orderItem: OrderItem){
        orderItemDao.insertOrderItem(orderItem)
    }

    suspend fun getOrderItemsByOrderId(orderId: Int): List<OrderItem>{
        return orderItemDao.getOrderItemsByOrderId(orderId)
    }
}