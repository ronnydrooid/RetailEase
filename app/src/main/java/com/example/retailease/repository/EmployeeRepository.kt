package com.example.retailease.repository

import com.example.retailease.datasource.dao.EmployeeDao
import com.example.retailease.models.Employee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(private val employeeDao: EmployeeDao) {
    suspend fun insertEmployee(employee: Employee){
        employeeDao.insertEmployee(employee)
    }
    suspend fun deleteEmployee(employee: Employee){
        employeeDao.deleteEmployee(employee)
    }
    suspend fun getEmployeeById(employeeId: Int): Employee {
        return employeeDao.getEmployeeById(employeeId)
    }

    fun getAllEmployees(): Flow<List<Employee>> {
        return employeeDao.getAllEmployees().flowOn(Dispatchers.IO).conflate()

    }
}