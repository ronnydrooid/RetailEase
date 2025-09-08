//package com.example.retailease.ui.screens.retail.pos
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil3.compose.AsyncImage
//import com.example.retailease.models.Order
//import com.example.retailease.models.OrderItem
//import com.example.retailease.ui.uiModels.DraftOrderItem
//import com.example.retailease.models.Product
//import com.example.retailease.ui.screens.retail.pos.checkout.OrderViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PosScreen(
//    padding: PaddingValues,
//    posViewModel: POSViewModel = hiltViewModel(),
//    orderViewModel: OrderViewModel = hiltViewModel(),
//    onCheckOutClick: (Int) -> Unit
//
//) {
//    val cartItems by remember{ derivedStateOf { posViewModel.cartItems }}
//    val showCart by posViewModel.showCartDetails
//    val totalPrice by remember { derivedStateOf { cartItems.sumOf { it.quantity * it.unitPrice } } }
//    val products = posViewModel.productsListState.collectAsStateWithLifecycle()
//
//    Column(
//        modifier = Modifier
//            .padding(padding)
//            .fillMaxSize()
//    ) {
//        FilterRow()
//        ProductGrid(
//            products = products.value,
//            onAdd = { posViewModel.addProductToCart(it) }
//        )
//    }
//
//    if (showCart) {
//        ModalBottomSheet(onDismissRequest = { posViewModel.dismissCartDetails() }) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text("Cart Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//                Spacer(modifier = Modifier.height(8.dp))
//
//                cartItems.forEach { item ->
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp)
//                    ) {
//                        Text(item.productName)
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            IconButton(onClick = { posViewModel.removeProductFromCart(item.toProduct()) }) {
//                                Icon(Icons.Default.Remove, contentDescription = "Remove")
//                            }
//                            Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp))
//                            IconButton(onClick = { posViewModel.addProductToCart(item.toProduct()) }) {
//                                Icon(Icons.Default.Add, contentDescription = "Add")
//                            }
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text("Total: ₹ ${"%.2f".format(totalPrice)}", fontWeight = FontWeight.Bold)
//                    Button(onClick = {
//                        val order = Order(
//                            totalPrice = totalPrice,
//                            totalQuantity = cartItems.sumOf { it.quantity }
//                        )
//                        orderViewModel.placeOrder(order,cartItems){orderId ->
//                            onCheckOutClick(orderId)
//                        }
//
//                    }) {
//                        Icon(Icons.Default.ShoppingCart, contentDescription = "Checkout")
//                        Spacer(Modifier.width(8.dp))
//                        Text("Checkout")
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun FilterRow() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        var expanded by remember { mutableStateOf(false) }
//        Box(modifier = Modifier.weight(1f)) {
//            Text(
//                text = "All Products",
//                modifier = Modifier
//                    .clickable { expanded = true }
//                    .background(Color.White, RoundedCornerShape(4.dp))
//                    .padding(12.dp)
//            )
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                DropdownMenuItem({ Text("Product A") }, onClick = { /* TODO */ })
//                DropdownMenuItem({ Text("Product B") }, onClick = { /* TODO */ })
//            }
//        }
//        IconButton(onClick = { /* TODO search */ }) {
//            Icon(
//                Icons.Default.Search,
//                contentDescription = "Search"
//            )
//        }
//        IconButton(onClick = { /* TODO barcode */ }) {
//            Icon(
//                Icons.Default.List,
//                contentDescription = "Scan"
//            )
//        }
//        IconButton(onClick = { /* TODO list view */ }) {
//            Icon(
//                Icons.Default.Star,
//                contentDescription = "List"
//            )
//        }
//    }
//}
//
//@Composable
//fun ProductGrid(
//    products: List<Product>,
//    onAdd: (Product) -> Unit,
//) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = Modifier.padding(horizontal = 12.dp),
//        contentPadding = PaddingValues(bottom = 80.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(
//            products,
//            key = { product ->
//                product.productId
//            }) { product ->
//            ProductCard(product, onAdd)
//        }
//    }
//}
//
//@Composable
//fun ProductCard(
//    product: Product,
//    onAdd: (Product) -> Unit,
//) {
//    Card(
//        shape = RoundedCornerShape(8.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Column(modifier = Modifier.padding(8.dp)) {
//            AsyncImage(
//                model = product.imagePath,
//                contentDescription = product.productName,
//                modifier = Modifier
//                    .height(100.dp)
//                    .fillMaxWidth(),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(Modifier.height(8.dp))
//            Text(product.productName, fontWeight = FontWeight.SemiBold)
//            Spacer(Modifier.height(4.dp))
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    "₹ ${"%.2f".format(product.price)}",
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF1668F2)
//                )
//                IconButton(onClick = { onAdd(product) }) {
//                    Icon(Icons.Default.AddCircle, contentDescription = "Add")
//                }
//            }
//        }
//    }
//}
//
//fun DraftOrderItem.toProduct(): Product {
//    return Product(
//        productId = this.productId,
//        productName = this.productName,
//        price = this.unitPrice,
//        imagePath = "",// fallback or optional
//    )
//}
//
//fun DraftOrderItem.toOrderItem(orderId: Int): OrderItem{
//    return OrderItem(
//        orderId = orderId,
//        productId = this.productId,
//        productName = this.productName,
//        quantity = this.quantity,
//        unitPrice = this.unitPrice
//    )
//}
