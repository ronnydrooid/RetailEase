package com.example.retailease.ui.components

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.example.retailease.backup.RetailEaseBackup
import com.example.retailease.datasource.room.AppDatabase
import com.example.retailease.repository.ProductRepository
import com.example.retailease.repository.SalesmanRepository
import com.example.retailease.repository.WholesaleProductRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class JsonBackupManager @Inject constructor(
    private val productRepository: ProductRepository,
    private val wholesaleProductRepository: WholesaleProductRepository,
    private val salesmanRepository: SalesmanRepository,
    private val database: AppDatabase
) {
    private suspend fun createBackup(): RetailEaseBackup {

        val productsList = productRepository.getAllProducts().first()
        val wholesaleProductsList = wholesaleProductRepository.getAllWholesaleProducts().first()
        val salesmanList = salesmanRepository.getAllSalesmen().first()

        return RetailEaseBackup(
            productsList,
            wholesaleProductsList,
            salesmanList,
        )
    }

    private suspend fun restoreBackup(backup: RetailEaseBackup) {
        database.withTransaction {
            productRepository.deleteAllProducts()
            wholesaleProductRepository.deleteAllWholesaleProducts()
            salesmanRepository.deleteAllSalesmen()

            productRepository.insertProducts(backup.products)
            wholesaleProductRepository.insertWholesaleProducts(backup.wholesaleProducts)
            salesmanRepository.insertSalesmen(backup.salesmen)
        }
    }

    suspend fun restoreFromStorage(
        context: Context,
        sourceUri: Uri,
    ): Result<String>{
        return try {
            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                val backupJson = inputStream.bufferedReader().use { it.readText() }
                val backup = Json.decodeFromString<RetailEaseBackup>(backupJson)
                restoreBackup(backup)
                Result.success("Json successfully created at $sourceUri")
            } ?: Result.failure(Exception("Failed to open input stream"))
        }
            catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun createBackupJson(): String {
        val backup = createBackup()
        val json = Json.encodeToString(backup)
        return json
    }

    fun saveToStorage(
        context: Context,
        destinationUri: Uri,
        backup: String,
    ): Result<String> {
        return try {
            context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                outputStream.write(backup.toByteArray())
                outputStream.flush()
                Result.success("Json successfully created at $destinationUri")
            } ?: Result.failure(Exception("Failed to open output stream"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}