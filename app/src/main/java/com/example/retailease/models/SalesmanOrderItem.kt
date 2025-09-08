package com.example.retailease.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "salesman_order_items",
    foreignKeys = [
        ForeignKey(
            entity = SalesmanOrder::class,
            parentColumns = ["salesmanOrderId"],
            childColumns = ["salesmanOrderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WholesaleProduct::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SalesmanOrderItem(
    @PrimaryKey(autoGenerate = true)
    val salesmanOrderItemId: Int = 0,
    val salesmanOrderId: Int,
    val productId: Int?,
    val quantity: BigDecimal,
    val quantity2: BigDecimal,
)



