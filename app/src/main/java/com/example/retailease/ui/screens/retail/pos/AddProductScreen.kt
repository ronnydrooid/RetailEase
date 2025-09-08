//package com.example.retailease.ui.screens.retail.pos
//
//import android.net.Uri
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil3.compose.rememberAsyncImagePainter
//import com.example.retailease.models.Product
//import com.example.retailease.ui.uiModels.toBigDecimalSafe
//
//@Composable
//fun AddProductScreen(
//    padding: PaddingValues,
//    posViewModel: POSViewModel = hiltViewModel(),
//    goBack: () -> Unit
//) {
//    val context = LocalContext.current
//
//    var productName by remember { mutableStateOf("") }
//    var quantity by remember { mutableStateOf("") }
//    var price by remember { mutableStateOf("") }
//    var category by remember { mutableStateOf("") }
//    var imageUri by remember { mutableStateOf<Uri?>(null) }
//
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        imageUri = uri
//    }
//
//    Column(
//        modifier = Modifier
//            .padding(padding)
//            .padding(16.dp)
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        OutlinedTextField(
//            value = productName,
//            onValueChange = { productName = it },
//            label = { Text("Product Name") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = quantity,
//            onValueChange = { quantity = it },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            label = { Text("Quantity") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = price,
//            onValueChange = { price = it },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            label = { Text("Price") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = category,
//            onValueChange = { category = it },
//            label = { Text("Category") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//
//        Button(
//            onClick = { imagePickerLauncher.launch("image/*") },
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ) {
//            Text("Select Product Image")
//        }
//
//        imageUri?.let { uri ->
//            Image(
//                painter = rememberAsyncImagePainter(model = uri),
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//                    .padding(8.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = {
//                if (productName.isNotBlank() && quantity.isNotBlank() && price.isNotBlank()) {
//                    val product = Product(
//                        productName = productName,
//                        quantity = quantity.toIntOrNull() ?: 0,
//                        price = price.toBigDecimalSafe(),
//                        imagePath = imageUri?.toString(),
//                        category = category,
//                    )
//                    posViewModel.addProduct(product)
//                    goBack()
//                } else {
//                    Toast.makeText(context, "Fill in all required fields", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            },
//            modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
//        ) {
//            Icon(Icons.Default.Check, contentDescription = "Save")
//            Text("Save Product")
//        }
//    }
//}
//
