package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.retailease.models.DraftOrderItem

@Dao
interface DraftOrderItemDao {

    @Upsert
    suspend fun insertDraftOrderItem(draftOrderItem: DraftOrderItem)

    @Query("DELETE FROM draft_order_items WHERE draftOrderId = :draftOrderId")
    suspend fun deleteDraftOrderItems(draftOrderId: Int)

    @Query("SELECT * FROM draft_order_items WHERE draftOrderId = :draftOrderId")
    suspend fun getDraftOrderItemsById(draftOrderId: Int): List<DraftOrderItem>
}