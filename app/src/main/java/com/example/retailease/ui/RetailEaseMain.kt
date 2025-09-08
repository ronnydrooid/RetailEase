package com.example.retailease.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Countertops
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Countertops
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.retailease.ui.components.BiometricPromptManager
import com.example.retailease.ui.components.RetailEaseTopBar
import com.example.retailease.ui.screens.retail.customer.AddCustomerScreen
import com.example.retailease.ui.screens.retail.customer.CustomerScreen
import com.example.retailease.ui.screens.wholesale.admin.AdminScreen
import com.example.retailease.ui.screens.wholesale.calculator.CalculatorCheckoutScreen
import com.example.retailease.ui.screens.wholesale.calculator.CalculatorScreen
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.CalculatorViewModel
import com.example.retailease.ui.screens.wholesale.calculator.DraftOrderHistoryScreen
import com.example.retailease.ui.screens.wholesale.khatabook.KhatabookHistoryScreen
import com.example.retailease.ui.screens.wholesale.khatabook.SalesmanKhataScreen
import com.example.retailease.ui.screens.wholesale.salesman.SalesmanCheckoutScreen
import com.example.retailease.ui.screens.wholesale.salesman.SalesmanScreen
import com.example.retailease.ui.screens.wholesale.settings.SettingsScreen
import com.example.retailease.ui.screens.wholesale.wsPos.SalesmanDraftOrderHistoryScreen
import com.example.retailease.ui.screens.wholesale.wsPos.WsPOSScreen
import com.example.retailease.ui.screens.wholesale.wsPos.WsPOSViewModel
import com.example.retailease.ui.uiModels.MenuItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetailEaseMain() {
    // Modal Navigation Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Navigation
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = currentBackStackEntry?.destination?.route

    // ViewModel
    val wsPosViewModel: WsPOSViewModel = hiltViewModel()

    val context = LocalContext.current
    val activity = context as? AppCompatActivity

    val biometricPromptManager by lazy {
        BiometricPromptManager(activity = activity!!)
    }
    val biometricResult by biometricPromptManager.result.collectAsState(null)

    // State to track pending navigation route
    val pendingNavigationRoute = remember { mutableStateOf<String?>(null) }

    // Handle biometric authentication results
    // ✅ FIXED: Handle biometric authentication results with proper state reset
    LaunchedEffect(biometricResult) {
        biometricResult?.let { result ->
            when (result) {
                BiometricPromptManager.BiometricResult.AuthenticationSucceeded -> {
                    pendingNavigationRoute.value?.let { route ->
                        val targetRoute = when (route) {
                            "Salesman_Khatabook" -> "Salesman_Khata_List"
                            "Admin" -> "Admin_Panel"
                            else -> route
                        }
                        navController.navigate(targetRoute) {
                            launchSingleTop = true
                        }
                        Toast.makeText(context, "Authentication Successful", Toast.LENGTH_SHORT).show()

                        // ✅ CRITICAL FIX: Reset biometric result after successful navigation
                        biometricPromptManager.resetResult()
                    }
                    pendingNavigationRoute.value = null
                }
                else -> {
                    // Show error messages for failed authentication
                    val message = when (result) {
                        BiometricPromptManager.BiometricResult.AuthenticationCancelled -> "Authentication Canceled by User"
                        is BiometricPromptManager.BiometricResult.AuthenticationError -> "Authentication Error: ${result.errorMessage}"
                        BiometricPromptManager.BiometricResult.AuthenticationFailed -> "Authentication Failed"
                        BiometricPromptManager.BiometricResult.AuthenticationNotSetInSettings -> "Authentication Not Set"
                        BiometricPromptManager.BiometricResult.BiometricUnavailable -> "Fingerprint not available"
                        BiometricPromptManager.BiometricResult.FeatureUnavailable -> "Feature not available"
                        else -> "Unknown authentication error"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    pendingNavigationRoute.value = null

                    // ✅ CRITICAL FIX: Reset biometric result after handling error
                    biometricPromptManager.resetResult()
                }
            }
        }
    }

    val menuItems = listOf(
        MenuItem(
            1,
            "Salesman_Management",
            "Salesman",
            Icons.Outlined.WorkOutline,
            Icons.Default.Work
        ),
        MenuItem(
            2,
            "Calculator",
            "Calculator",
            Icons.Outlined.Calculate,
            Icons.Default.Calculate
        ),
        MenuItem(
            3,
            "Salesman_Khatabook",
            "Khatabook", // ✅ This is the title that requires authentication
            Icons.Outlined.AccountBalanceWallet,
            Icons.Filled.AccountBalanceWallet
        ),
        MenuItem(
            4,
            "Admin",
            "Admin", // ✅ This is the title that requires authentication
            Icons.Outlined.AdminPanelSettings,
            Icons.Default.AdminPanelSettings
        ),
        MenuItem(
            5,
            "Settings",
            "Settings",
            Icons.Outlined.Settings,
            Icons.Default.Settings
        ),
    )

    val selectedItemIndex = when (currentScreen) {
        "Salesman" -> 0
        "Calculator" -> 1
        "Khatabook" -> 2
        "Admin" -> 3
        "Settings" -> 4
        else -> -1
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentScreen == "Salesman",
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "RetailEase",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    HorizontalDivider()

                    // ✅ PUBLIC ACCESS SECTION
                    Text(
                        "General Access",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Public access menu items (Salesman & Calculator)
                    menuItems.take(2).forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = { Text(item.title) },
                            icon = {
                                Icon(
                                    if (item.title == currentScreen) item.selectedIcon else item.icon,
                                    contentDescription = item.title,
                                )
                            },
                            selected = index == selectedItemIndex,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }

                    // ✅ DIVIDER BETWEEN SECTIONS
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // ✅ OWNER ONLY SECTION
                    Text(
                        "Owner Access Only",
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error // Red color to indicate restricted access
                    )

                    // Owner-only menu items (Khatabook, Admin, Settings)
                    menuItems.drop(2).forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.title)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // ✅ Lock icon to indicate authentication required
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Authentication Required",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            icon = {
                                Icon(
                                    if (item.title == currentScreen) item.selectedIcon else item.icon,
                                    contentDescription = item.title
                                )
                            },
                            selected = (index + 2) == selectedItemIndex,
                            onClick = {
                                scope.launch { drawerState.close() }

                                when (item.route) {
                                    "Salesman_Khatabook", "Admin" -> {
                                        pendingNavigationRoute.value = item.route
                                        val screenName = when (item.route) {
                                            "Salesman_Khatabook" -> "Khatabook"
                                            "Admin" -> "Admin Panel"
                                            else -> item.title
                                        }
                                        biometricPromptManager.showBiometricPrompt(
                                            "$screenName Access",
                                            "Unlock to continue"
                                        )
                                    }
                                    else -> {
                                        navController.navigate(item.route) {
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    ) {
        if (currentScreen?.startsWith("Admin") == true) {
            // When in admin screen, just show AdminScreen *without* Scaffold
            AdminScreen(
                navController = navController
            )
        } else {
            val scrollBehaviour =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
                topBar = {
                    RetailEaseTopBar(
                        onBackClick = {
                            if (currentScreen?.startsWith("Calculator_Checkout") == true) {
                                navController.navigate("Calculator_Panel/${null}") {
                                    popUpTo("Calculator") {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            } else if (currentScreen?.startsWith("POSScreen") == true) {
                                navController.popBackStack()
                                wsPosViewModel.clearCart()
                                wsPosViewModel.enterCash("")
                            } else {
                                navController.popBackStack()
                            }
                        },
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        },
                        onNavigate = {
                            if (currentScreen?.startsWith("Calculator") == true) {
                                navController.navigate("Draft_History") {
                                    launchSingleTop = true
                                }
                            } else if (currentScreen?.startsWith("POSScreen") == true) {
                                val salesmanId = currentBackStackEntry?.arguments?.getString("id")
                                navController.navigate("Salesman_Draft_History/$salesmanId") {
                                    launchSingleTop = true
                                }
                            }
                        },
                        currentScreen = currentScreen,
                        scrollBehavior = scrollBehaviour
                    )
                },
                bottomBar = {}
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = "Salesman_Management"
                ) {
                    navigation(startDestination = "Salesman_Khata_List", route = "Salesman_Khatabook") {
                        composable("Salesman_Khata_List") {
                            SalesmanKhataScreen(padding) {
                                navController.navigate("Khatabook/${it}")
                            }
                        }

                        composable("Khatabook/{id}") {
                            val salesmanId = it.arguments?.getString("id")?.toIntOrNull()
                            KhatabookHistoryScreen(padding, salesmanId = salesmanId)
                        }
                    }

                    navigation(startDestination = "Salesman", route = "Salesman_Management") {
                        composable("Salesman") {
                            SalesmanScreen(padding) {
                                navController.navigate("POSScreen/${it}")
                            }
                        }
                        composable("POSScreen/{id}") {
                            BackHandler {
                                navController.popBackStack()
                                wsPosViewModel.clearCart()
                                wsPosViewModel.enterCash("")
                            }
                            val salesmanId = it.arguments?.getString("id")?.toIntOrNull()
                            WsPOSScreen(padding, wsPosViewModel) {
                                wsPosViewModel.dismissCartDetails()
                                navController.navigate("Salesman_Checkout/${salesmanId}/${null}")
                            }
                        }
                        composable("Salesman_Draft_History/{id}") {
                            val salesmanId = it.arguments?.getString("id")?.toIntOrNull()
                            SalesmanDraftOrderHistoryScreen(padding, salesmanId) { salesmanDraftOrderId ->
                                navController.navigate("Salesman_Checkout/${salesmanId}/${salesmanDraftOrderId}") {
                                    popUpTo("Salesman_Draft_History/{id}") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                        composable("Salesman_Checkout/{id1}/{id2}") {
                            val salesmanId = it.arguments?.getString("id1")?.toIntOrNull()
                            val salesmanDraftOrderId = it.arguments?.getString("id2")?.toIntOrNull()

                            if (salesmanId != null) {
                                SalesmanCheckoutScreen(
                                    padding,
                                    salesmanId = salesmanId,
                                    wsPosViewModel = wsPosViewModel,
                                    salesmanDraftOrderId = salesmanDraftOrderId
                                ) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }

                    navigation(startDestination = "Customers", route = "Customers_Management") {
                        composable("Customers") {
                            CustomerScreen(padding)
                        }
                        composable("Add_Customers") {
                            AddCustomerScreen(padding) {
                                navController.popBackStack()
                            }
                        }
                    }

                    navigation(startDestination = "Admin_Panel", route = "Admin") {
                        composable("Admin_Panel") {
                            AdminScreen(
                                navController = navController
                            )
                        }
                    }

                    navigation(startDestination = "Calculator_Panel/{id}", route = "Calculator") {
                        composable("Calculator_Panel/{id}") { backStackEntry ->
                            val draftOrderId =
                                backStackEntry.arguments?.getString("id")?.toIntOrNull()
                            val parentEntry =
                                remember { navController.getBackStackEntry("Calculator") }
                            val viewModel = hiltViewModel<CalculatorViewModel>(parentEntry)

                            CalculatorScreen(padding, viewModel, draftOrderId) {
                                navController.navigate("Calculator_Checkout/${draftOrderId}") {
                                    launchSingleTop = true
                                }
                            }
                        }
                        composable("Draft_History") {
                            DraftOrderHistoryScreen(padding) {
                                navController.navigate("Calculator_Panel/${it}") {
                                    popUpTo("Draft_History") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                        composable("Calculator_Checkout/{id}") { backStackEntry ->
                            val draftOrderId =
                                backStackEntry.arguments?.getString("id")?.toIntOrNull()

                            BackHandler {
                                navController.navigate("Calculator_Panel/${null}") {
                                    popUpTo("Calculator_Checkout/${draftOrderId}") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                            val parentEntry =
                                remember { navController.getBackStackEntry("Calculator") }
                            val viewModel = hiltViewModel<CalculatorViewModel>(parentEntry)

                            CalculatorCheckoutScreen(padding, viewModel, draftOrderId) {
                                navController.navigate("Calculator_Panel/${null}") {
                                    popUpTo("Calculator_Checkout/${draftOrderId}") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }

                    composable("Settings") {
                        SettingsScreen(padding)
                    }
                }
            }
        }
    }
}
