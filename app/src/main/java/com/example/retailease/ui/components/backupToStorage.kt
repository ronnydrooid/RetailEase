package com.example.retailease.ui.components

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.io.FileInputStream

fun backupToStorage(
    context: Context,
    uri: Uri,
    dbFileName: String = "RetailEase.db",
): Result<String> {

    return try {

        val dbPath = context.getDatabasePath(dbFileName)

        if (!dbPath.exists()) {
            Toast.makeText(context, "Database not found", Toast.LENGTH_SHORT).show()
            return Result.failure(Exception("Database not found at : $dbPath"))
        }

        context.contentResolver.openOutputStream(uri)?.use { output ->
            FileInputStream(dbPath).use { input ->
                val bytesWritten = input.copyTo(output)
                output.flush()
                Result.success("$bytesWritten bytes written to $uri")
            }
        } ?: Result.failure(Exception("Failed to open output stream"))
    } catch (e: Exception) {
        Result.failure(e)
    }


}

