package com.example.retailease.repository

import com.example.retailease.datasource.dao.WholesaleProductDao
import com.example.retailease.models.WholesaleProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WholesaleProductRepository @Inject constructor(private val wholesaleProductDao: WholesaleProductDao) {
    suspend fun insertWholesaleProduct(wholesaleProduct: WholesaleProduct) {
        wholesaleProductDao.insertWholesaleProduct(wholesaleProduct)
    }

    suspend fun insertWholesaleProducts(wholesaleProducts: List<WholesaleProduct>){
        wholesaleProductDao.insertWholesaleProducts(wholesaleProducts)
    }

    suspend fun deleteWholesaleProduct(wholesaleProduct: WholesaleProduct) {
        wholesaleProductDao.deleteWholesaleProduct(wholesaleProduct)
    }

    suspend fun deleteAllWholesaleProducts() {
        wholesaleProductDao.deleteAllWholesaleProducts()
    }

    fun getAllWholesaleProducts(): Flow<List<WholesaleProduct>> {
        return wholesaleProductDao.getAllWholesaleProducts().flowOn(Dispatchers.IO).conflate()
    }

    suspend fun getWholesaleProductById(productId: Int): WholesaleProduct {
        return wholesaleProductDao.getWholesaleProductById(productId)
    }


}