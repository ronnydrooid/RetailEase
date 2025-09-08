package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.retailease.models.WholesaleProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface WholesaleProductDao {
    @Upsert
    suspend fun insertWholesaleProduct(wholesaleProduct: WholesaleProduct)

    @Insert
    suspend fun insertWholesaleProducts(wholesaleProducts: List<WholesaleProduct>)

    @Delete
    suspend fun deleteWholesaleProduct(wholesaleProduct: WholesaleProduct)

    @Query("DELETE FROM wholesale_products")
    suspend fun deleteAllWholesaleProducts()

    @Query("SELECT * FROM wholesale_products where productId = :productId")
    suspend fun getWholesaleProductById(productId: Int): WholesaleProduct

    @Query("SELECT * from wholesale_products")
    fun getAllWholesaleProducts(): Flow<List<WholesaleProduct>>
}