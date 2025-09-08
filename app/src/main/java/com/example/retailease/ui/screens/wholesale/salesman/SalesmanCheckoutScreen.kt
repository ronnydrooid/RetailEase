package com.example.retailease.ui.screens.wholesale.salesman

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.retailease.ui.components.PrintManager
import com.example.retailease.ui.components.handleBluetoothPrintButton
import com.example.retailease.ui.screens.wholesale.wsPos.WsPOSViewModel
import com.example.retailease.ui.components.clean
import com.example.retailease.ui.components.hasValidQuantity
import com.example.retailease.ui.components.toBigDecimalSafe
import com.example.retailease.ui.screens.wholesale.calculator.CalculatorScreen
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.CalculatorViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesmanCheckoutScreen(
    padding: PaddingValues = PaddingValues(),
    salesmanId: Int,
    salesmanViewModel: SalesmanViewModel = hiltViewModel(),
    wsPosViewModel: WsPOSViewModel = hiltViewModel(),
    calculatorViewModel: CalculatorViewModel = hiltViewModel(),
    salesmanDraftOrderId: Int? = null,
    onSuccess: () -> Unit,
) {

    LaunchedEffect(salesmanId, salesmanDraftOrderId) {
        // Always get salesman info if salesmanId is available
        if (salesmanId != null) {
            salesmanViewModel.getSalesmanById(salesmanId)
        }

        // Only load draft order if salesmanDraftOrderId is available
        if (salesmanDraftOrderId != null) {
            wsPosViewModel.loadSalesmanDraftOrder(salesmanDraftOrderId)
        }
    }

    val selectedSalesman = salesmanViewModel.selectedSalesman.collectAsStateWithLifecycle().value
    val wsPosItems = wsPosViewModel.wsPosItems.collectAsStateWithLifecycle().value
    val cash = wsPosViewModel.cash.collectAsStateWithLifecycle().value
    val calculatorItems = calculatorViewModel.calculatorOrderItems.collectAsStateWithLifecycle().value

    val cartDetails = salesmanViewModel.cartDetails.collectAsStateWithLifecycle().value
    var checked by rememberSaveable { mutableStateOf(true) }
    var addCash by rememberSaveable { mutableStateOf(true) }
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    // Fixed: Request multiple Bluetooth permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            try {
                handleBluetoothPrintButton(context) {
                    wsPosViewModel.handlePrinting(salesmanId) {
                        salesmanViewModel.printOrderByBluetooth(context, it)
                    }
                }
            } catch (_: Exception) {
                Toast.makeText(context,"Some Unknown error occured",Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Bluetooth permissions are required for printing", Toast.LENGTH_LONG).show()
        }
    }

    // Helper function to check if all required Bluetooth permissions are granted
    fun hasAllBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getRequiredBluetoothPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Order") },
            text = { Text("Are you sure you want to save this order permanently?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        if (salesmanId != null) {
                            wsPosViewModel.placeSalesmanFinalOrder(salesmanId = salesmanId)
                            onSuccess()
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(
                start = padding.calculateStartPadding(LocalLayoutDirection.current),
                end = padding.calculateEndPadding(LocalLayoutDirection.current),
                top = padding.calculateTopPadding(),

                )
            .imePadding()
            .navigationBarsPadding()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp, start = 10.dp , end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "Order Summary",
                    style = MaterialTheme.typography.headlineMedium,
                )
                AssistChip(
                    onClick = { salesmanViewModel.toggleCartDetails() },
                    label = {
                        Row() {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add more stuff",
                                modifier = Modifier.size(18.dp)

                            )
                            Text("Add More")
                        }
                    }

                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Product")
                Text("200Gm")
                Text("500Gm")
                Text("Total")
            }

            HorizontalDivider()

            wsPosItems.filter {
                it.hasValidQuantity()
            }.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        item.productName,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        item.quantity.toBigDecimalSafe().clean(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        item.quantity2.toBigDecimalSafe().clean(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "₹${
                            item.quantity.toBigDecimalSafe().multiply(item.unitPrice).plus(
                                item.quantity2.toBigDecimalSafe().multiply(item.unitPrice2)
                            ).clean()
                        }",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
                HorizontalDivider()
            }

            // 🧮 Total at the bottom of scrollable column
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    buildAnnotatedString {
                        append("Qty : ")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) { // Green
                            append("${wsPosViewModel.totalCount.collectAsStateWithLifecycle().value.clean()}kg")
                        }
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = 15.dp)
                )
                Text(
                    buildAnnotatedString {
                        append("Total : ")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) { // Pink
                            append("₹${wsPosViewModel.totalCartValue.collectAsStateWithLifecycle().value.clean()}")
                        }
                    },
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text("Add Discount :")


                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            if (checked) {
                wsPosViewModel.addTotalDiscountedAmount(
                    selectedSalesman?.discount?.toBigDecimal() ?: BigDecimal.ZERO
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        buildAnnotatedString {
                            append("Discounted Qty : ")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) { // Green
                                append("${wsPosViewModel.totalDiscountedItems.collectAsStateWithLifecycle().value}kg")
                            }
                        },
                        modifier = Modifier.padding(end = 15.dp)
                    )
                    Text(
                        buildAnnotatedString {
                            append("Total Discount : ")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) { // Pink
                                append(
                                    "₹${wsPosViewModel.totalDiscountedAmount.collectAsStateWithLifecycle().value} "
                                )
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        buildAnnotatedString {
                            append("Final Value : ")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) { // Green
                                append("₹${wsPosViewModel.finalValue.collectAsStateWithLifecycle().value.clean()}")
                            }
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }

            Row(
                modifier = if (addCash) {
                    Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                } else {
                    Modifier.fillMaxWidth()
                },
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Cash",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable {
                            if (cash.isNotEmpty()) {
                                wsPosViewModel.enterCash("")
                            }
                            addCash = !addCash
                        }
                )

                if (addCash) {
                    TextField(
                        value = cash,
                        onValueChange = { wsPosViewModel.enterCash(it) },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CurrencyRupee,
                                contentDescription = "Rupee"
                            )
                        },
                        placeholder = { Text("Enter amount") },
                        modifier = Modifier.weight(1f), // Takes available space nicely
                    )
                }
            }
            if (addCash) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        buildAnnotatedString {
                            append("Remaining : ")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) { // Green
                                append(
                                    "₹${
                                        wsPosViewModel.finalValue.collectAsStateWithLifecycle().value.subtract(
                                            cash.toBigDecimalSafe()
                                        ).clean()
                                    }"
                                )
                            }
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            textDecoration = TextDecoration.Underline,
                        )
                    )
                }
            }


        }

        // 🔘 Action buttons below
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedButton(
                onClick = {
                    if (salesmanId != null) {
                        wsPosViewModel.placeSalesmanDraftOrder(
                            salesmanDraftOrderId = salesmanDraftOrderId,
                            salesmanId = salesmanId
                        )
                        onSuccess()
                    }
                },
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    "Save Draft", style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 4.dp)
                )
            }

            Button(
                onClick = {
                    showConfirmDialog = true
                },
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),

                ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Save", style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 4.dp)
                )
            }

            Button(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)

                    if (!hasAllBluetoothPermissions()) {
                        permissionLauncher.launch(getRequiredBluetoothPermissions())
                        return@Button
                    }

                    try {
                        handleBluetoothPrintButton(context) {
                            wsPosViewModel.handlePrinting(salesmanId) {
                                salesmanViewModel.printOrderByBluetooth(context, it)
                            }
                        }
                    } catch (_: Exception) {
                        Toast.makeText(context,"Some Unknown error occured",Toast.LENGTH_SHORT).show()
                    }

                },
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Print",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Print", style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 4.dp)
                )
            }
        }

    }

    if(cartDetails){
        ModalBottomSheet(
            onDismissRequest = {salesmanViewModel.dismissCartDetails()},
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            CalculatorScreen {
                wsPosViewModel.addMoreItemsAtCheckout(calculatorItems)
                salesmanViewModel.dismissCartDetails()
                calculatorViewModel.clearAll()
            }
        }
    }
}