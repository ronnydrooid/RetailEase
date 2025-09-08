package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.retailease.models.DraftOrder
import com.example.retailease.models.SalesmanDraftOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesmanDraftOrderDao {
    @Upsert
    suspend fun insertSalesmanDraftOrder(salesmanDraftOrder: SalesmanDraftOrder): Long

    @Delete
    suspend fun deleteSalesmanDraftOrder(salesmanDraftOrder: SalesmanDraftOrder)

    @Query("SELECT * from salesman_draft_orders")
    fun getSalesmanDraftOrders(): Flow<List<SalesmanDraftOrder>>

    @Query("SELECT * from salesman_draft_orders WHERE salesmanDraftOrderId = :salesmanDraftOrderId")
    suspend fun getSalesmanDraftOrderById(salesmanDraftOrderId: Int): SalesmanDraftOrder

}