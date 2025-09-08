package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.retailease.models.Salesman
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesmanDao {
    @Upsert
    suspend fun insertSalesman(salesman: Salesman)

    @Insert
    suspend fun insertSalesmen(salesmen: List<Salesman>)

    @Delete
    suspend fun deleteSalesman(salesman: Salesman)

    @Query("DELETE FROM salesmen")
    suspend fun deleteAllSalesmen()

    @Query("SELECT * from salesmen")
    fun getAllSalesmen(): Flow<List<Salesman>>

    @Query("SELECT * from salesmen where salesmanId = :salesmanId")
    suspend fun getSalesmanById(salesmanId: Int): Salesman
}