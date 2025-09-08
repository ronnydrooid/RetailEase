//package com.example.retailease.ui.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ShoppingCart
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import java.math.BigDecimal
//
//@Composable
//fun POSBottomBar(
//    totalCount: BigDecimal,
//    totalPrice: BigDecimal,
//    onClick: () -> Unit
//) {
////    if (totalCount > 0) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
//                .clickable { onClick() }
//                .padding(16.dp)
//                .navigationBarsPadding()
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text("${totalCount.clean()} items", fontWeight = FontWeight.Bold)
//                    Text("₹ ${totalPrice.clean()}")
//                }
//                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
//            }
//        }
////    }
//}
