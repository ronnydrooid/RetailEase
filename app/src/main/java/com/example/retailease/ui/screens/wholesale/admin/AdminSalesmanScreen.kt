package com.example.retailease.ui.screens.wholesale.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.retailease.models.Salesman
import com.example.retailease.ui.screens.wholesale.admin.adminViewModels.AdminSalesmanViewModel


@Composable
fun AdminSalesmanScreen(
    salesmanViewModel: AdminSalesmanViewModel = hiltViewModel(),
    onAdd: (Int?) -> Unit,
) {
    val salesmanList = salesmanViewModel.salesmanList.collectAsStateWithLifecycle().value
    Box(modifier = Modifier.fillMaxSize()) {
        SalesmanList(
            salesmanList = salesmanList,
            onEdit = { salesmanId -> onAdd(salesmanId) }) {
            salesmanViewModel.deleteSalesman(it)
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            onClick = { onAdd(null) }
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Salesman")
                Text("Add Salesman")
            }
        }
    }

}

@Composable
fun SalesmanList(
    salesmanList: List<Salesman>,
    onEdit: (Int) -> Unit,
    onDelete: (Salesman) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(salesmanList) { salesman ->
            Column(modifier = Modifier.fillMaxWidth()) {
                SalesmanRow(
                    salesman = salesman, onEdit = { onEdit(salesman.salesmanId) }) {
                    onDelete(salesman)
                }

                HorizontalDivider(Modifier.fillMaxWidth())

            }
        }
    }
}

@Composable
fun SalesmanRow(
    salesman: Salesman,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = salesman.imagePath,
            contentDescription = salesman.name,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = salesman.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2
            )
            salesman.phoneNumber?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onEdit() }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Product",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { onDelete() }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Product",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}