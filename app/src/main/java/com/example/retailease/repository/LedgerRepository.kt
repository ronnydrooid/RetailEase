package com.example.retailease.repository

import androidx.room.PrimaryKey
import com.example.retailease.datasource.dao.LedgerDao
import com.example.retailease.models.SalesmanLedger
import com.example.retailease.models.SalesmanOrder
import com.example.retailease.models.TransactionType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LedgerRepository @Inject constructor(private val ledgerDao: LedgerDao) {
    suspend fun insertLedgerEntry(ledgerEntry: SalesmanLedger) {
        ledgerDao.insertLedgerEntry(ledgerEntry)
    }

    suspend fun getCurrentBalance(salesmanId: Int): BigDecimal {
        return ledgerDao.getCurrentBalance(salesmanId) ?: BigDecimal.ZERO
    }

    fun getLedgerBySalesman(salesmanId: Int): Flow<List<SalesmanLedger>> {
        return ledgerDao.getLedgerBySalesman(salesmanId)
    }

    suspend fun processOrderForLedger(salesmanOrder: SalesmanOrder) {
        val currentBalance = getCurrentBalance(salesmanOrder.salesmanId)

//      Creating Debit entry for the order Created
        val debitEntryForBillCreated = createDebitEntry(
            salesmanOrder,
            "Bill Created for Order ID: ${salesmanOrder.salesmanOrderId}",
            currentBalance
        )
        ledgerDao.insertLedgerEntry(debitEntryForBillCreated)

        val balanceAfterDebit = debitEntryForBillCreated.runningBalance ?: BigDecimal.ZERO
//      Checking how much payment is done
        when{
            salesmanOrder.cashGiven.compareTo(BigDecimal.ZERO) == 0 ->{

            }
            salesmanOrder.cashGiven.compareTo(salesmanOrder.finalPrice) < 0 ->{
                val creditEntryForRemainingAmount = createCreditEntry(
                    salesmanOrder,
                    "Partial payment made for Order ID: ${salesmanOrder.salesmanOrderId}",
                    balanceAfterDebit
                )
                ledgerDao.insertLedgerEntry(creditEntryForRemainingAmount)
            }
            salesmanOrder.cashGiven.compareTo(salesmanOrder.finalPrice) == 0 ->{
                val creditEntryForPaidAmount = createCreditEntry(
                    salesmanOrder,
                    "Full payment made for Order ID: ${salesmanOrder.salesmanOrderId}",
                    balanceAfterDebit
                )
                ledgerDao.insertLedgerEntry(creditEntryForPaidAmount)
            }
            salesmanOrder.cashGiven.compareTo(salesmanOrder.finalPrice) > 0 ->{
                val fullPaymentCredit = createCreditEntry(
                    salesmanOrder.copy(cashGiven = salesmanOrder.finalPrice),
                    "Full payment made for Order ID: ${salesmanOrder.salesmanOrderId}",
                    balanceAfterDebit
                )
                ledgerDao.insertLedgerEntry(fullPaymentCredit)

                val advanceAmount = salesmanOrder.cashGiven.subtract(salesmanOrder.finalPrice)

                val advanceCredit = SalesmanLedger(
                    salesmanId = salesmanOrder.salesmanId,
                    relatedOrderId = salesmanOrder.salesmanOrderId,
                    amount = advanceAmount,
                    transactionType = TransactionType.CREDIT,
                    description = "Advance Payment for next order",
                    runningBalance = (fullPaymentCredit.runningBalance?: BigDecimal.ZERO).subtract(advanceAmount)
                )
                ledgerDao.insertLedgerEntry(advanceCredit)
            }
        }


    }
}

private fun createDebitEntry(
    salesmanOrder: SalesmanOrder,
    description: String?,
    currentBalance: BigDecimal,
): SalesmanLedger {
    return SalesmanLedger(
        salesmanId = salesmanOrder.salesmanId,
        relatedOrderId = salesmanOrder.salesmanOrderId,
        amount = salesmanOrder.finalPrice,
        transactionType = TransactionType.DEBIT,
        description = description,
        runningBalance = currentBalance.add(salesmanOrder.finalPrice)
    )
}
private fun createCreditEntry(
    salesmanOrder: SalesmanOrder,
    description: String?,
    currentBalance: BigDecimal,
): SalesmanLedger {
    return SalesmanLedger(
        salesmanId = salesmanOrder.salesmanId,
        relatedOrderId = salesmanOrder.salesmanOrderId,
        amount = salesmanOrder.cashGiven,
        transactionType = TransactionType.CREDIT,
        description = description,
        runningBalance = currentBalance.subtract(salesmanOrder.cashGiven)
    )
}