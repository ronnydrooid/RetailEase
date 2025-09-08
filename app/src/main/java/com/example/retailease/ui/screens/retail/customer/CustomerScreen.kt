package com.example.retailease.ui.screens.retail.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.retailease.models.Customer

@Composable
fun CustomerScreen(
    padding: PaddingValues,
    customerViewModel: CustomerViewModel = hiltViewModel(),
) {
    var searchText by remember { mutableStateOf("") }
    val customers = customerViewModel.customerListState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        CustomerFilterRow(
            searchText = searchText,
            onSearchChange = { searchText = it }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filteredCustomers = customers.value.filter {
                it.name.contains(searchText, ignoreCase = true)
            }
            items(filteredCustomers) { customer ->
                CustomerCard(customer)
            }
        }
    }
}


@Composable
fun CustomerFilterRow(
    searchText: String,
    onSearchChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchText,
            onValueChange = onSearchChange,
            placeholder = { Text("Search Customer") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CustomerCard(customer: Customer) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(customer.name.first().toString(), color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(customer.name, fontWeight = FontWeight.Bold)
                        customer.email?.let {
                            Text(it, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
                Icon(Icons.Default.MoreVert, contentDescription = "Actions")
            }
            Spacer(modifier = Modifier.height(8.dp))
            customer.phone?.let { Text("📞 $it") }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Loyalty Points: ${customer.loyaltyPoints}", fontSize = 12.sp)
                Text("Total Purchases: $${customer.totalPurchases}", fontSize = 12.sp)
            }
        }
    }
}
