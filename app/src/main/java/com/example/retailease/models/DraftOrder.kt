package com.example.retailease.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "draft_orders")
data class DraftOrder(
    @PrimaryKey(autoGenerate = true)
    val draftOrderId: Int = 0,
    val totalPrice: BigDecimal,
    val totalQuantity: BigDecimal,
    val draftOrderTime: Date = Date()
)
