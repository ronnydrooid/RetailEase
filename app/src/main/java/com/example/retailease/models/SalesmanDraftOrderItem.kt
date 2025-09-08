package com.example.retailease.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal


@Entity(tableName = "salesman_draft_order_items",
    foreignKeys = [
        ForeignKey(
            entity = SalesmanDraftOrder::class,
            parentColumns = ["salesmanDraftOrderId"],
            childColumns = ["salesmanDraftOrderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WholesaleProduct::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE

        )
    ])
data class SalesmanDraftOrderItem(
    @PrimaryKey(autoGenerate = true)
    val salesmanDraftOrderItemId: Int = 0,
    val salesmanDraftOrderId: Int,
    val productId: Int?,
    val productName: String,
    val quantity1: BigDecimal,
    val unitPrice1: BigDecimal,
    val quantity2: BigDecimal,
    val unitPrice2: BigDecimal,
    val isSalesmanDiscountEnabled: Boolean = true,
)


