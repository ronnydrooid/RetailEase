package com.example.retailease.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetailEaseTopBar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onNavigate: () -> Unit,
    currentScreen: String?,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {

    val title = when (currentScreen) {
        "Salesman_Khata_List" -> "Khatabook"
        "Salesman" -> "Salesman"
        "Customers" -> "Customers"
        "Add_Customers" -> "Add Customers"
        "AdminProducts", "Salesman_Panel", "Labor" -> "Admin Panel"
        "Draft_History" -> "Draft Orders"
        "Settings" -> "Settings"
        else -> {
            when (true) {
                currentScreen?.startsWith("POSScreen") -> "POS"
                currentScreen?.startsWith("Salesman_Checkout") -> "Checkout"
                currentScreen?.startsWith("Add_Products") -> "Add Products"
                currentScreen?.startsWith("Add_Wholesale_Products") -> "Add Wholesale Products"
                currentScreen?.startsWith("Add_Salesman") -> "Add Salesman"
                currentScreen?.startsWith("Calculator_Panel") -> "Calculator"
                currentScreen?.startsWith("Calculator_Checkout") -> "Checkout"
                currentScreen?.startsWith("Salesman_Draft_History") -> "Salesman Draft Orders"
                currentScreen?.startsWith("Khatabook") -> "Khatabook History"
                else -> "RetailEase"

            }
        }
    }
    val showBack: Boolean = when (currentScreen) {
        "Add_Products" -> true
        "Add_Wholesale_Products" -> true
        "Add_Customers" -> true
        "AdminProducts" -> true
        "Salesman_Panel" -> true
        "Labor" -> true
        "Calculator_Checkout" -> true
        "Draft_History" -> true
        "Settings" -> true
        "Salesman_Khata_List" -> true

        else -> {
            when (true) {
                currentScreen?.startsWith("POSScreen") -> true
                currentScreen?.startsWith("Salesman_Checkout") -> true
                currentScreen?.startsWith("Add_Products") -> true
                currentScreen?.startsWith("Add_Wholesale_Products") -> true
                currentScreen?.startsWith("Add_Salesman") -> true
                currentScreen?.startsWith("Calculator_Checkout") -> true
                currentScreen?.startsWith("Calculator_Panel") -> true
                currentScreen?.startsWith("Salesman_Draft_History") -> true
                currentScreen?.startsWith("Khatabook") -> true
                else -> false
            }


        }
    }
    val showMenu =
        currentScreen != null && !showBack

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
            when {

                showBack -> IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                showMenu -> IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        },
        actions = {
            if (currentScreen?.startsWith("Calculator_Panel") == true || currentScreen?.startsWith("POSScreen") == true) {
                IconButton(onClick = onNavigate) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        scrollBehavior = scrollBehavior
    )
}
