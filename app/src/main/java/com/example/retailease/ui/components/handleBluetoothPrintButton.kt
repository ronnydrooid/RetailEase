package com.example.retailease.ui.components

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun handleBluetoothPrintButton(context: Context, onPrint: () -> Unit) {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
    val bluetoothAdapter = bluetoothManager?.adapter

    if (bluetoothAdapter == null) {
        Toast.makeText(context, "Bluetooth not available", Toast.LENGTH_SHORT).show()
        return
    }
    // Check all required Bluetooth permissions
    if (!hasAllRequiredBluetoothPermissions(context)) {
        Toast.makeText(context, "Missing Bluetooth permissions", Toast.LENGTH_SHORT).show()
        return
    }

    if (!bluetoothAdapter.isEnabled) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

        if (context is Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            context.startActivity(enableBtIntent)
            Toast.makeText(context, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }
    }


    onPrint()

}

private fun hasAllRequiredBluetoothPermissions(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+ requires both BLUETOOTH_SCAN and BLUETOOTH_CONNECT
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
    } else {
        // Older Android versions
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) == PackageManager.PERMISSION_GRANTED
    }
}
