package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.retailease.models.OrderItem

@Dao
interface OrderItemDao {
    @Insert
    suspend fun insertOrderItem(orderItem: OrderItem)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsByOrderId(orderId: Int): List<OrderItem>
}