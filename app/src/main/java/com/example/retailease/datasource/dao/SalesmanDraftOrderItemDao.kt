package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.retailease.models.SalesmanDraftOrderItem

@Dao
interface SalesmanDraftOrderItemDao {
    @Upsert
    suspend fun insertSalesmanDraftOrderItems(salesmanDraftOrderItems: List<SalesmanDraftOrderItem>)

    @Query("DELETE FROM salesman_draft_order_items WHERE salesmanDraftOrderId = :salesmanDraftOrderId")
    suspend fun deleteSalesmanDraftOrderItems(salesmanDraftOrderId: Int)

    @Query("SELECT * FROM salesman_draft_order_items WHERE salesmanDraftOrderId = :salesmanDraftOrderId")
    suspend fun getSalesmanDraftOrderItemsById(salesmanDraftOrderId: Int): List<SalesmanDraftOrderItem>

}