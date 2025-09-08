package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.retailease.models.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT * FROM orders where orderId = :orderId")
    suspend fun getOrderById(orderId: Int): Order

    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders ORDER BY orderId DESC LIMIT 1")
    fun getLatestOrder(): Flow<Order?>
}