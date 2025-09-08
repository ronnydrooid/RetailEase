package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.retailease.models.SalesmanDraftOrderItem
import com.example.retailease.models.SalesmanOrderItem

@Dao
interface SalesmanOrderItemDao {
    @Upsert
    suspend fun insertSalesmanOrderItems(salesmanOrderItems: List<SalesmanOrderItem>)

    @Query("SELECT * FROM salesman_order_items WHERE salesmanOrderId = :salesmanOrderId")
    suspend fun getSalesmanOrderItemsByOrderId(salesmanOrderId: Int): List<SalesmanOrderItem>

}