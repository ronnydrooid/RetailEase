package com.example.retailease.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "draft_order_items",
    foreignKeys = [
        ForeignKey(
            entity = DraftOrder::class,
            parentColumns = ["draftOrderId"],
            childColumns = ["draftOrderId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE,
        )
    ])
data class DraftOrderItem(
    @PrimaryKey(autoGenerate = true)
    val draftOrderItemId: Int = 0,
    val draftOrderId: Int,
    val productId: Int? = null,
    val productName: String,
    val quantity: BigDecimal,
    val unitPrice: BigDecimal
)