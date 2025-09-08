package com.example.retailease.ui.screens.wholesale.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.retailease.models.DraftOrder
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.DraftOrderHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DraftOrderHistoryScreen(
    padding: PaddingValues = PaddingValues(),
    draftOrderHistoryViewModel: DraftOrderHistoryViewModel = hiltViewModel(),
    onClick: (Int) -> Unit
) {
    val draftOrders = draftOrderHistoryViewModel.draftOrders.collectAsStateWithLifecycle().value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(padding),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(draftOrders) {
            DraftOrderCard(draftOrder = it, onClick = {
                onClick(it.draftOrderId)
            }, onDeleteClick = {
                draftOrderHistoryViewModel.deleteDraftOrder(it)
            })
        }
    }
}

@Composable
fun DraftOrderCard(
    draftOrder: DraftOrder,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit, // Callback when delete button is pressed
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(draftOrder.draftOrderTime)
    val formattedTime = timeFormat.format(draftOrder.draftOrderTime)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Draft Order #${draftOrder.draftOrderId}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDeleteClick() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Date: $formattedDate")
                    Text("Time: $formattedTime")
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Qty: ${draftOrder.totalQuantity}")
                    Text(
                        "Total Price: ₹${draftOrder.totalPrice}",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}