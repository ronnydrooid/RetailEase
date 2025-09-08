package com.example.retailease.ui.components

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.FileOutputStream

fun restoreFromStorage(context: Context, uri: Uri, dbFileName: String = "RetailEase.db"): Result<String> {
    return try {
        val dbPath = context.getDatabasePath(dbFileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(dbPath).use { output ->
                val bytesWritten = input.copyTo(output)
                output.flush()
                Result.success("Restore completed: $bytesWritten bytes restored")
            }
        } ?: Result.failure(Exception("Failed to open input stream"))

    } catch (e: Exception) {
        Log.e("Restore", "Restore failed", e)
        Result.failure(e)
    }
}