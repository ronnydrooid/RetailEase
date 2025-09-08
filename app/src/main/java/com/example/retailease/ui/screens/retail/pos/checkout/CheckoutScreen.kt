//package com.example.retailease.ui.screens.retail.pos.checkout
//
//import android.widget.Toast
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material3.Button
//import androidx.compose.material3.Divider
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.retailease.models.Product
//import com.example.retailease.ui.components.generatePdf
//
//@Composable
//fun CheckOutScreen(
//    padding: PaddingValues,
//    id: Int,
//    orderViewModel: OrderViewModel = hiltViewModel(),
//) {
//    LaunchedEffect(id) {
//        orderViewModel.loadOrder(id)
//    }
//
//    val order = orderViewModel.order.collectAsStateWithLifecycle().value
//    val orderItems = orderViewModel.orderItems.collectAsStateWithLifecycle().value
//    val context = LocalContext.current
//    Column(
//        modifier = Modifier
//            .padding(padding)
//            .padding(16.dp)
//            .fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "🧾 Receipt Summary",
//            style = MaterialTheme.typography.headlineMedium
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        order?.let {
//            Column(horizontalAlignment = Alignment.Start) {
//                Text("Order ID: ${it.orderId}", fontSize = 16.sp)
//                Text("Customer: ${it.customerName ?: "N/A"}", fontSize = 16.sp)
//                Text("Handled By: ${it.handledByEmployeeName ?: "N/A"}", fontSize = 16.sp)
//                Text("Total Quantity: ${it.totalQuantity}", fontSize = 16.sp)
//                Text("Order Date: ${it.orderTime}", fontSize = 14.sp)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            HorizontalDivider(thickness = 1.dp)
//
//            // Header Row
//            Row(
//                Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text("Item", modifier = Modifier.weight(1f))
//                Text("Qty", modifier = Modifier.weight(0.5f))
//                Text("Price", modifier = Modifier.weight(0.8f))
//                Text("Subtotal", modifier = Modifier.weight(1f))
//            }
//
//            HorizontalDivider(thickness = 1.dp)
//
//            // List of Order Items
//            LazyColumn {
//                items(orderItems) { item ->
//                    Row(
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(item.productName, modifier = Modifier.weight(1f))
//                        Text("${item.quantity}", modifier = Modifier.weight(0.5f))
//                        Text("₹${item.unitPrice}", modifier = Modifier.weight(0.8f))
//                        Text("₹${item.unitPrice * item.quantity}", modifier = Modifier.weight(1f))
//                    }
//                }
//            }
//
//            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))
//
//            Row(
//                Modifier
//                    .fillMaxWidth()
//                    .padding(top = 12.dp),
//                horizontalArrangement = Arrangement.End
//            ) {
//                Text(
//                    "Total: ₹${it.totalPrice}",
//                    style = MaterialTheme.typography.titleLarge
//                )
//            }
//        } ?: run {
//            Text("No order data found.")
//        }
//        Button(
//            onClick = {
//              generatePdf(order, orderItems, context)
//            },
//            modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
//        ) {
//            Icon(Icons.Default.Check, contentDescription = "Save")
//            Text("Save PDF")
//        }
//    }
//}
