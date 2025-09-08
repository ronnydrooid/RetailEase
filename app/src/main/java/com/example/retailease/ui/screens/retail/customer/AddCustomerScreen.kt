package com.example.retailease.ui.screens.retail.customer

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.retailease.models.Customer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(
    padding: PaddingValues,
    customerViewModel: CustomerViewModel = hiltViewModel(),
    onSaveClick: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var loyaltyPoints by remember { mutableStateOf("") }
    var totalPurchases by remember { mutableStateOf("") }

    val context = LocalContext.current


    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = loyaltyPoints,
            onValueChange = { loyaltyPoints = it },
            label = { Text("Loyalty Points") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = totalPurchases,
            onValueChange = { totalPurchases = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Total Purchases") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val points = loyaltyPoints.toDoubleOrNull() ?: 0.0
                val purchases = totalPurchases.toDoubleOrNull() ?: 0.0

                if (name.isNotBlank()) {
                    val customer = Customer(
                        name = name,
                        email = email.ifBlank { null },
                        phone = phone.ifBlank { null },
                        loyaltyPoints = points,
                        totalPurchases = purchases
                    )
                    customerViewModel.addCustomer(customer)
                    onSaveClick()

                } else {
                    Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = "Save")
            Text("Add Customer")
        }
    }
}
