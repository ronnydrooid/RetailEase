package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.retailease.models.SalesmanLedger
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface LedgerDao {
    @Insert
    suspend fun insertLedgerEntry(ledgerEntry: SalesmanLedger)

    @Query("SELECT runningBalance FROM salesman_ledger WHERE salesmanId = :salesmanId ORDER BY transactionDate DESC LIMIT 1")
    suspend fun getCurrentBalance(salesmanId: Int): BigDecimal?

    @Query("SELECT * FROM salesman_ledger WHERE salesmanID = :salesmanId ORDER BY transactionDate DESC ")
    fun getLedgerBySalesman(salesmanId: Int): Flow<List<SalesmanLedger>>
}