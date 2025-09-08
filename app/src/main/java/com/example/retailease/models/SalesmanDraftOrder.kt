package com.example.retailease.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(
    tableName = "salesman_draft_orders",
    foreignKeys = [
        ForeignKey(
            entity = Salesman::class,
            parentColumns = ["salesmanId"],
            childColumns = ["salesmanId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SalesmanDraftOrder(
    @PrimaryKey(autoGenerate = true)
    val salesmanDraftOrderId: Int = 0,
    val salesmanId: Int,
    val totalPrice: BigDecimal,
    val totalQuantity: BigDecimal,
    val totalDiscount: BigDecimal,
    val finalPrice: BigDecimal,
    val cashGiven: BigDecimal,
    val draftOrderTime: Date = Date()
)
