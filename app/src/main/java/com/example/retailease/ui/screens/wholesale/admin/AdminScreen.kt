package com.example.retailease.ui.screens.wholesale.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.retailease.ui.components.RetailEaseTopBar
import com.example.retailease.ui.screens.wholesale.admin.adminProductsScreen.AdminAddProductScreen
import com.example.retailease.ui.screens.wholesale.admin.adminProductsScreen.AdminAddWholesaleProductScreen
import com.example.retailease.ui.screens.wholesale.admin.adminProductsScreen.AdminProductScreen
import com.example.retailease.ui.uiModels.TabItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController? = null,
) {
//    Navigation
    val tabNavController = rememberNavController()
    val currentBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentScreen = currentBackStackEntry?.destination?.route

    val tabItems = listOf(
        TabItem(
            title = "Products",
            route = "AdminProducts",
            unselectedIcon = Icons.Outlined.LibraryAdd,
            selectedIcon = Icons.Filled.LibraryAdd
        ),
        TabItem(
            title = "Salesman",
            route = "Salesman_Panel",
            unselectedIcon = Icons.Outlined.PeopleAlt,
            selectedIcon = Icons.Filled.PeopleAlt
        ),
        TabItem(
            title = "Labor",
            route = "Labor",
            unselectedIcon = Icons.Outlined.Badge,
            selectedIcon = Icons.Filled.Badge
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            RetailEaseTopBar(
                onBackClick = {
                    when (currentScreen) {
                        "AdminProducts" -> {
                            navController?.popBackStack()
                        }

                        "Salesman_Panel" -> {
                            tabNavController.navigate("Products")
                        }

                        "Labor" -> {
                            tabNavController.navigate("Products")
                        }

                        else -> {
                            tabNavController.popBackStack()
                        }
                    }
                },
                onMenuClick = {
                },
                onNavigate = {},
                currentScreen = currentScreen,
            )
        })
    { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            if (currentScreen != null) {
                if (!currentScreen.startsWith("Add_Products") && !currentScreen.startsWith("Add_Wholesale_Products") && !currentScreen.startsWith("Add_Salesman")
                ) {
                    PrimaryTabRow(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        selectedTabIndex = tabItems.indexOfFirst { it.route == currentScreen }
                            .takeIf { it >= 0 } ?: 0,
                    ) {
                        tabItems.forEach { tabItem ->
                            Tab(
                                selected = currentScreen == tabItem.route,
                                onClick = {
                                    if (currentScreen != tabItem.route) {
                                        tabNavController.navigate(tabItem.route) {
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                text = {
                                    Text(
                                        text = tabItem.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.W700
                                        )
                                    )
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (tabItem.route == currentScreen) tabItem.selectedIcon else tabItem.unselectedIcon,
                                        contentDescription = tabItem.title
                                    )
                                }
                            )

                        }
                    }
                }
            }








            NavHost(
                navController = tabNavController,
                startDestination = "Products"
            ) {
                navigation(startDestination = "AdminProducts", route = "Products") {

                    composable("AdminProducts") {
                        BackHandler {
                            navController?.popBackStack()
                        }
                        AdminProductScreen(
                            onAddRetail = {
                                tabNavController.navigate("Add_Products/${it}")
                            },
                            onAddWholesale = {
                                tabNavController.navigate("Add_Wholesale_Products/${it}")
                            }
                        )

                    }
                    composable("Add_Products/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                        AdminAddProductScreen(id) {
                            tabNavController.popBackStack()
                        }

                    }
                    composable("Add_Wholesale_Products/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                        AdminAddWholesaleProductScreen(id) {
                            tabNavController.popBackStack()
                        }

                    }
                }
                navigation(startDestination = "Salesman_Panel", route = "Salesman") {
                    composable("Salesman_Panel") {
                        BackHandler {
                            tabNavController.navigate("Products")
                        }
                        AdminSalesmanScreen() {
                            tabNavController.navigate("Add_Salesman/${it}")
                        }
                    }
                    composable("Add_Salesman/{id}") {
                        val salesmanId = it.arguments?.getString("id")?.toIntOrNull()

                        AdminAddSalesmanScreen(salesmanId) {
                            tabNavController.popBackStack()
                        }

                    }
                }
                composable("Labor") {
                    BackHandler {
                        tabNavController.navigate("Products")
                    }
                    Column(modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        Text("Feature coming soon ....")
                    }
                }

            }

        }
    }

}
