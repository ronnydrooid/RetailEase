package com.example.retailease.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(
    tableName = "salesman_ledger",
    foreignKeys = [
        ForeignKey(
            entity = Salesman::class,
            parentColumns = ["salesmanId"],
            childColumns = ["salesmanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SalesmanOrder::class,
            parentColumns = ["salesmanOrderId"],
            childColumns = ["relatedOrderId"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [

        Index(value = ["salesmanId"]),
        Index(value = ["transactionDate"]),
        Index(value = ["transactionType"])

    ]
)
data class SalesmanLedger(
    @PrimaryKey(autoGenerate = true)
    val ledgerId: Int = 0,
    val salesmanId: Int,
    val relatedOrderId: Int? = null,
//    Transaction Details
    val amount: BigDecimal,
    val transactionType: TransactionType,
    val description: String?,
    val transactionDate: Date = Date(),
//    Current Balance
    val runningBalance: BigDecimal?,

    )

enum class TransactionType {
    DEBIT,
    CREDIT
}