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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.retailease.models.WholesaleProduct
import com.example.retailease.ui.components.saveImageToInternalStorage
import com.example.retailease.ui.screens.wholesale.admin.adminViewModels.AdminProductViewModel
import com.example.retailease.ui.components.toBigDecimalSafe
import java.io.File
import java.util.UUID

@Composable
fun AdminAddWholesaleProductScreen(
    productId: Int?,
    viewModel: AdminProductViewModel = hiltViewModel(),
    goBack: () -> Unit,
) {
    val context = LocalContext.current

    val selectedWholesaleProduct = viewModel.selectedWholesaleProduct.collectAsStateWithLifecycle().value

    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.getWholesaleProductById(productId)
        }
    }

    var wholesaleProductId by rememberSaveable { mutableStateOf<Int?>(null) }
    var productName by rememberSaveable { mutableStateOf("") }
    var wholesalePrice by rememberSaveable { mutableStateOf("") }
    var wholesalePrice2 by rememberSaveable { mutableStateOf("") }
    var salesmanPrice by rememberSaveable { mutableStateOf("") }
    var salesmanPrice2 by rememberSaveable { mutableStateOf("") }
    var isSalesmanDiscountEnabled by rememberSaveable { mutableStateOf(true) }
    var category by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var path by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(selectedWholesaleProduct) {
        selectedWholesaleProduct?.let {
            wholesaleProductId = it.productId
            productName = it.productName
            wholesalePrice = it.wholesalePrice200gm.toString()
            wholesalePrice2 = it.wholesalePrice500gm.toString()
            salesmanPrice = it.salesmanPrice200gm.toString()
            salesmanPrice2 = it.salesmanPrice500gm.toString()
            isSalesmanDiscountEnabled = it.isSalesmanDiscountEnabled
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

    val insets = WindowInsets.ime.union(WindowInsets.navigationBars)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {



            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = wholesalePrice,
                onValueChange = { wholesalePrice = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Wholesale Price 200gm") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = wholesalePrice2,
                onValueChange = { wholesalePrice2 = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Wholesale Price 500gm") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = salesmanPrice,
                onValueChange = { salesmanPrice = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Salesman Price 200gm") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = salesmanPrice2,
                onValueChange = { salesmanPrice2 = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Salesman Price 500gm") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    "Salesman Discount",
                    textAlign = TextAlign.Center
                )

                Switch(
                    checked = isSalesmanDiscountEnabled,
                    onCheckedChange = { isSalesmanDiscountEnabled = it },

                    thumbContent = {
                        if (isSalesmanDiscountEnabled) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "salesmanDiscount",
                            )
                        }
                    }
                )
            }

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
                        .size(200.dp)
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                    ,
                    contentScale = ContentScale.FillBounds
                )
            }


            Button(
                onClick = {
                    imageUri?.let {
                        if (it.toString().startsWith("content://")) {
                            val fileName = "wholesale_product_image_${UUID.randomUUID()}.jpg"
                            val imagePath = saveImageToInternalStorage(it, context, fileName)
                            path = imagePath.toString()
                        } else {
                            path = it.path.toString()
                        }
                    }
                    if (productName.isNotBlank() && wholesalePrice.isNotBlank() && salesmanPrice.isNotBlank()) {
                        val wholesaleProduct = WholesaleProduct(
                            productId = wholesaleProductId ?: 0,
                            productName = productName,
                            wholesalePrice200gm = wholesalePrice.toBigDecimalSafe(),
                            wholesalePrice500gm = wholesalePrice2.toBigDecimalSafe(),
                            salesmanPrice200gm = salesmanPrice.toBigDecimalSafe(),
                            salesmanPrice500gm = salesmanPrice2.toBigDecimalSafe(),
                            isSalesmanDiscountEnabled = isSalesmanDiscountEnabled,
                            imagePath = path.ifBlank { null },
                            category = category
                        )
                        viewModel.addWholesaleProduct(wholesaleProduct)
                        goBack()
                    } else {
                        Toast.makeText(context, "Fill in all required fields", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
                Text("Save Product")
            }
        }
    }
}
