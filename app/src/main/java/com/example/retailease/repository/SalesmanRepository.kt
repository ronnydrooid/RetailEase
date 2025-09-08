package com.example.retailease.repository

import com.example.retailease.datasource.dao.SalesmanDao
import com.example.retailease.models.Salesman
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesmanRepository @Inject constructor(private val salesmanDao: SalesmanDao) {

    suspend fun insertSalesman(salesman: Salesman){
        salesmanDao.insertSalesman(salesman)
    }

    suspend fun insertSalesmen(salesmen: List<Salesman>){
        salesmanDao.insertSalesmen(salesmen)
    }

    suspend fun deleteSalesman(salesman: Salesman){
        salesmanDao.deleteSalesman(salesman)
    }

    suspend fun deleteAllSalesmen(){
        salesmanDao.deleteAllSalesmen()
    }

    suspend fun getSalesmanById(salesmanId: Int): Salesman{
        return salesmanDao.getSalesmanById(salesmanId)
    }

    fun getAllSalesmen(): Flow<List<Salesman>> {
        return salesmanDao.getAllSalesmen()
    }

}