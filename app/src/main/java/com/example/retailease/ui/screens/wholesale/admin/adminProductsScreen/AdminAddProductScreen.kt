package com.example.retailease.ui.screens.wholesale.admin.adminProductsScreen


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.retailease.models.Product
import com.example.retailease.ui.components.saveImageToInternalStorage
import com.example.retailease.ui.screens.wholesale.admin.adminViewModels.AdminProductViewModel
import com.example.retailease.ui.components.toBigDecimalSafe
import java.io.File
import java.util.*

@Composable
fun AdminAddProductScreen(
    productId: Int?,
    viewmodel: AdminProductViewModel = hiltViewModel(),
    goBack: () -> Unit,
) {
    val context = LocalContext.current

    val product = viewmodel.selectedProduct.collectAsStateWithLifecycle().value

    LaunchedEffect(productId) {
        if (productId != null) {
            viewmodel.getProductById(productId)
        }
    }

    var selectedProductId by rememberSaveable { mutableStateOf<Int?>(null) }
    var productName by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var path by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(product) {
        product?.let {
            selectedProductId = it.productId
            productName = it.productName
            quantity = it.quantity.toString()
            price = it.price.toString()
            category = it.category ?: ""
            imageUri = it.imagePath?.let { path -> Uri.fromFile(File(path)) }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(
                start = 16.dp,
                end = 16.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )


        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Select Product Image")
        }

        imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                imageUri?.let {
                    if (it.toString().startsWith("content://")) {
                        val fileName = "product_image_${UUID.randomUUID()}.jpg"
                        path = saveImageToInternalStorage(it, context, fileName).toString()
                    } else {
                        path = it.path.toString()
                    }
                }
                if (productName.isNotBlank() && quantity.isNotBlank() && price.isNotBlank()) {
                    val productt = Product(
                        productId = selectedProductId ?: 0,
                        productName = productName,
                        quantity = quantity.toIntOrNull() ?: 0,
                        price = price.toBigDecimalSafe(),
                        imagePath = path,
                        category = category
                    )
                    viewmodel.addProduct(productt)
                    goBack()
                } else {
                    Toast.makeText(context, "Fill in all required fields", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = "Save")
            Text("Save Product")
        }
    }
}

