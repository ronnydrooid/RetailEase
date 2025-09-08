//package com.example.retailease.ui.components
//
//import android.content.ActivityNotFoundException
//import android.content.ContentValues
//import android.content.Context
//import android.content.Intent
//import android.graphics.Paint
//import android.graphics.pdf.PdfDocument
//import android.os.Environment
//import android.provider.MediaStore
//import android.widget.Toast
//import com.example.retailease.models.Order
//import com.example.retailease.models.OrderItem
//
//fun generatePdf(
//    order: Order?,
//    orderItems: List<OrderItem>,
//    context: Context
//) {
//    if (order == null) return
//
//    val pdfDocument = PdfDocument()
//    val pageInfo = PdfDocument.PageInfo.Builder(620, 875, 1).create()
//    val page = pdfDocument.startPage(pageInfo)
//    val canvas = page.canvas
//    val paint = Paint()
//
//    var yPosition = 50f
//
//    // Header
//    paint.textAlign = Paint.Align.CENTER
//    paint.textSize = 24f
//    paint.isFakeBoldText = true
//    canvas.drawText("RetailEase Supermart", canvas.width / 2f, yPosition, paint)
//
//    yPosition += 30f
//    paint.textSize = 16f
//    canvas.drawText("Receipt Details", canvas.width / 2f, yPosition, paint)
//
//    yPosition += 40f
//    paint.textAlign = Paint.Align.LEFT
//    paint.isFakeBoldText = false
//    paint.textSize = 14f
//
//    canvas.drawText("Customer: ${order.customerName ?: "N/A"}", 30f, yPosition, paint)
//    yPosition += 20f
//    canvas.drawText("Handled By: ${order.handledByEmployeeName ?: "N/A"}", 30f, yPosition, paint)
//    yPosition += 20f
//    canvas.drawText("Order Time: ${order.orderTime}", 30f, yPosition, paint)
//    yPosition += 30f
//
//    // Table Header
//    paint.isFakeBoldText = true
//    canvas.drawText("Product", 30f, yPosition, paint)
//    canvas.drawText("Qty", 250f, yPosition, paint)
//    canvas.drawText("Unit Price", 330f, yPosition, paint)
//    canvas.drawText("Total", 470f, yPosition, paint)
//
//    yPosition += 15f
//    paint.strokeWidth = 1f
//    canvas.drawLine(30f, yPosition, canvas.width - 30f, yPosition, paint)
//    yPosition += 20f
//    paint.isFakeBoldText = false
//
//    // Table Rows
//    orderItems.forEach { item ->
//        val itemTotal = item.unitPrice * item.quantity
//
//        canvas.drawText(item.productName, 30f, yPosition, paint)
//        canvas.drawText("${item.quantity}", 250f, yPosition, paint)
//        canvas.drawText("₹${String.format("%.2f", item.unitPrice)}", 330f, yPosition, paint)
//        canvas.drawText("₹${String.format("%.2f", itemTotal)}", 470f, yPosition, paint)
//        yPosition += 20f
//    }
//
//    yPosition += 20f
//    canvas.drawLine(30f, yPosition, canvas.width - 30f, yPosition, paint)
//
//    // Totals
//    yPosition += 30f
//    paint.isFakeBoldText = true
//    paint.textAlign = Paint.Align.RIGHT
//
//    canvas.drawText("Total Quantity: ${order.totalQuantity}", canvas.width - 30f, yPosition, paint)
//    yPosition += 20f
//    canvas.drawText("Total Price: ₹${String.format("%.2f", order.totalPrice)}", canvas.width - 30f, yPosition, paint)
//
//    pdfDocument.finishPage(page)
//
//    // Save PDF to file or share as needed
//    // For example: write to a fileOutputStream in app's internal storage
//    // val file = File(context.filesDir, "receipt.pdf")
//    // pdfDocument.writeTo(FileOutputStream(file))
//    val pdfFileName = "receipt_${order.orderId}.pdf"
//    val resolver = context.contentResolver
//    val contentValues = ContentValues().apply {
//        put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFileName)
//        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
//        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
//    }
//    val pdfUri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
//    pdfUri?.let {
//        resolver.openOutputStream(pdfUri).use {
//            pdfDocument.writeTo(it)
//            Toast.makeText(context, "PDF saved to Documents", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    pdfDocument.close()
//    val openIntent = Intent(Intent.ACTION_VIEW).apply {
//        setDataAndType(pdfUri, "application/pdf")
//        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
//    }
//    try {
//        context.startActivity(openIntent)
//    } catch (e: ActivityNotFoundException) {
//        Toast.makeText(context, "No PDF viewer found.", Toast.LENGTH_SHORT).show()
//    }
//
//}
