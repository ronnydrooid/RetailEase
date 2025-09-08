package com.example.retailease.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.retailease.models.Product
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductDao {
    @Upsert
    suspend fun insertProduct(product: Product)

    @Insert
    suspend fun insertProducts(product: List<Product>)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("SELECT * FROM products where productId = :productId")
    suspend fun getProductById(productId: Int): Product

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @RawQuery
    fun checkpoint(query: SupportSQLiteQuery): Int

}
