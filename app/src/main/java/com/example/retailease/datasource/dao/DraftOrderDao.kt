package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.retailease.models.DraftOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftOrderDao {
    @Upsert
    suspend fun insertDraftOrder(draftOrder: DraftOrder): Long

    @Delete
    suspend fun deleteDraftOrder(draftOrder: DraftOrder)

    @Query("SELECT * from draft_orders")
    fun getDraftOrders(): Flow<List<DraftOrder>>

    @Query("SELECT * from draft_orders where draftOrderId = :draftOrderId")
    suspend fun getDraftOrderById(draftOrderId: Int): DraftOrder
}