package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.retailease.models.Customer
import com.example.retailease.models.Employee
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM customers where customerId = :customerId")
    suspend fun getCustomerById(customerId: Int): Customer

    @Query("SELECT * FROM customers")
    fun getAllCustomers(): Flow<List<Customer>>

    @Insert
    suspend fun insertEmployee(employee: Employee)

}