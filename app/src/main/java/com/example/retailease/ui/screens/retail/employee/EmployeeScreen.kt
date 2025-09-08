package com.example.retailease.ui.screens.retail.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.retailease.models.Employee

@Composable
fun EmployeeScreen(
    padding: PaddingValues,
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
) {
    var searchText by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("All") }
    val employees = employeeViewModel.employeeListState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        EmployeeFilterRow(
            searchText = searchText,
            onSearchChange = { searchText = it },
            selectedDepartment = selectedDepartment,
            onDepartmentChange = { selectedDepartment = it }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filteredEmployees = employees.value.filter {
                (selectedDepartment == "All" || it.department == selectedDepartment) &&
                        it.name.contains(searchText, ignoreCase = true)
            }
            items(filteredEmployees) { employee ->
                EmployeeCard(employee)
            }
        }
    }
}


@Composable
fun EmployeeFilterRow(
    searchText: String,
    onSearchChange: (String) -> Unit,
    selectedDepartment: String,
    onDepartmentChange: (String) -> Unit,
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
            placeholder = { Text("Search Employee") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))

        var expanded by remember { mutableStateOf(false) }
        Box {
            Text(
                text = selectedDepartment,
                modifier = Modifier
                    .clickable { expanded = true }
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("All", "Admin", "Hardware", "Software", "Marketing").forEach { dept ->
                    DropdownMenuItem(onClick = {
                        onDepartmentChange(dept)
                        expanded = false
                    }, text = { Text(dept) })
                }
            }
        }
    }
}

@Composable
fun EmployeeCard(employee: Employee) {
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
                            .background(Color.Gray, CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = employee.imageUrl,
                            contentDescription = employee.name,
                            modifier = Modifier,
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(employee.name, fontWeight = FontWeight.Bold)
                        Text(employee.department, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Icon(Icons.Default.MoreVert, contentDescription = "Actions")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("📞 ${employee.phoneNumber}")
            employee.email?.let { Text("✉️  $it") }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AssistChip(onClick = {}, label = { Text("Requests: 3") })
                Text("Hire Date: 2019-06-06", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
