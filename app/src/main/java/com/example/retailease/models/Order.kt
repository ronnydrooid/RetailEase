package com.example.retailease.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val orderId: Int = 0,
    val totalPrice: BigDecimal,
    val customerName: String? = null,
    val handledByEmployeeName: String? = null,
    val totalQuantity: BigDecimal,
    val orderTime: Date = Date()
)
