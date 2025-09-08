package com.example.retailease.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.retailease.datasource.dao.ProductDao
import com.example.retailease.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ProductRepository @Inject constructor(private val productDao: ProductDao) {

    suspend fun insertProduct(product: Product){
        productDao.insertProduct(product)
    }

    suspend fun insertProducts(products: List<Product>){
        productDao.insertProducts(products)

    }

    suspend fun deleteProduct(product: Product){
        productDao.deleteProduct(product)
    }

    suspend fun deleteAllProducts(){
        productDao.deleteAllProducts()
    }

    suspend fun getProductById(productId: Int): Product{
        return productDao.getProductById(productId)
    }

    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().flowOn(Dispatchers.IO).conflate()
    }

    fun checkpoint(query: SupportSQLiteQuery): Int{
        return productDao.checkpoint(query)
    }

}