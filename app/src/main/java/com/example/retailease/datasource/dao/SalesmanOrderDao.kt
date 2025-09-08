package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.retailease.models.SalesmanDraftOrder
import com.example.retailease.models.SalesmanOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesmanOrderDao {
    @Insert
    suspend fun insertSalesmanOrder(salesmanOrder: SalesmanOrder): Long

    @Query("SELECT * FROM salesman_orders")
    fun getAllSalesmanOrders(): Flow<List<SalesmanOrder>>

    @Query("SELECT * FROM salesman_orders WHERE salesmanOrderId = :salesmanOrderId")
    suspend fun getSalesmanOrderById(salesmanOrderId: Int): SalesmanOrder
}

