package com.example.retailease.repository

import com.example.retailease.datasource.dao.CustomerDao
import com.example.retailease.models.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(private val customerDao: CustomerDao) {
    suspend fun insertCustomer(customer: Customer){
        customerDao.insertCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer){
        customerDao.deleteCustomer(customer)
    }

    suspend fun getCustomerById(customerId: Int): Customer{
        return customerDao.getCustomerById(customerId)
    }

    fun getAllCustomers(): Flow<List<Customer>> {
        return customerDao.getAllCustomers().flowOn(Dispatchers.IO).conflate()
    }
}