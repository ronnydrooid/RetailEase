package com.example.retailease.ui.screens.wholesale.admin

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
import com.example.retailease.models.Salesman
import com.example.retailease.ui.components.saveImageToInternalStorage
import com.example.retailease.ui.screens.wholesale.admin.adminViewModels.AdminSalesmanViewModel
import java.io.File
import java.util.*

@Composable
fun AdminAddSalesmanScreen(
    salesmanId: Int?,
    viewmodel: AdminSalesmanViewModel = hiltViewModel(),
    goBack: () -> Unit,
) {
    val context = LocalContext.current

    val salesman = viewmodel.selectedSalesman.collectAsStateWithLifecycle().value

    LaunchedEffect(salesmanId) {
        if (salesmanId != null) {
            viewmodel.getSalesmanById(salesmanId)
        }
    }

    var selectedSalesmanId by rememberSaveable { mutableStateOf<Int?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var path by rememberSaveable { mutableStateOf("") }


    LaunchedEffect(salesman) {
        salesman?.let {
            selectedSalesmanId = it.salesmanId
            name = it.name
            phoneNumber = it.phoneNumber ?: ""
            discount = it.discount.toString()
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
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = discount,
            onValueChange = { discount = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Discount ₹") },
            modifier = Modifier.fillMaxWidth()
        )


        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Select Salesman Image")
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
                        val fileName = "salesman_image_${UUID.randomUUID()}.jpg"
                        path = saveImageToInternalStorage(it, context, fileName).toString()
                    } else {
                        path = it.path.toString()
                    }
                }

                if (name.isNotBlank() && discount.isNotBlank()) {
                    val salesmanObj = Salesman(
                        salesmanId = selectedSalesmanId ?: 0,
                        name = name,
                        phoneNumber = phoneNumber,
                        discount = discount.toDoubleOrNull() ?: 0.0,
                        imagePath = path
                    )
                    viewmodel.insertSalesman(salesmanObj)
                    goBack()
                } else {
                    Toast.makeText(context, "Fill in all required fields", Toast.LENGTH_SHORT).show()
                }

            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = "Save")
            Text("Save Salesman")
        }
    }
}