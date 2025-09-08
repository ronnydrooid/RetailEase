package com.example.retailease.ui.components

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrintManager(private val context: Context) {
    private var lastTextToPrint: String = ""

    private val usbReceive = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action == ACTION_USB_PERMISSION) {
                synchronized(this) {
                    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager?
                    val usbDevice: UsbDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            p1.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            p1.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        }

                    if (p1.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            val printer = EscPosPrinter(
                                UsbConnection(usbManager, usbDevice),
                                203,
                                80f,
                                48
                            )
                            printer.printFormattedTextAndOpenCashBox(lastTextToPrint, 200)
                        }
                    }
                    context.unregisterReceiver(this)
                }
            }
        }

    }

    suspend fun printTextViaBluetooth(text: String, openCashDrawer: Boolean): Result<String> = withContext(Dispatchers.IO) {
        try {
            val printer = BluetoothPrintersConnections.selectFirstPaired()
            if (printer == null) {
                return@withContext Result.failure(Exception("No paired device found"))
            }

            val escPosPrinter = EscPosPrinter(printer, 203, 80f, 48)
            if (openCashDrawer) {
                escPosPrinter.printFormattedTextAndOpenCashBox(text, 200)
            } else {
                escPosPrinter.printFormattedTextAndCut(text, 200)
            }
            Result.success("Text printed successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun printTextViaUSB(text: String) {
        lastTextToPrint = text
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager?
        val usbConnection = UsbPrintersConnections.selectFirstConnected(context)

        if (usbConnection != null && usbManager != null) {
            val permissionIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(ACTION_USB_PERMISSION),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_MUTABLE else 0

            )
            val filter = IntentFilter(ACTION_USB_PERMISSION)

            context.registerReceiver(usbReceive, filter)
            usbManager.requestPermission(usbConnection.device, permissionIntent)
        } else {
            Toast.makeText(context, "No USB device found", Toast.LENGTH_SHORT).show()
            return
        }


    }

    companion object {
        private const val ACTION_USB_PERMISSION = "com.example.retailease.USB_PERMISSION"
    }

}