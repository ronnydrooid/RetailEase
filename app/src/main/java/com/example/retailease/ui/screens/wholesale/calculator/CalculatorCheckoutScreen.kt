package com.example.retailease.ui.screens.wholesale.calculator

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.retailease.ui.components.PrintManager
import com.example.retailease.ui.components.handleBluetoothPrintButton
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.CalculatorCheckoutViewModel
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.CalculatorViewModel
import com.example.retailease.ui.components.clean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Replace your existing CalculatorCheckoutScreen with this fixed version

@Composable
fun CalculatorCheckoutScreen(
    padding: PaddingValues = PaddingValues(),
    calculatorViewModel: CalculatorViewModel = hiltViewModel(),
    draftOrderId: Int? = null,
    calculatorCheckoutViewModel: CalculatorCheckoutViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
) {

    val calculatorOrderItems = calculatorViewModel.calculatorOrderItems.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val selectedType = calculatorViewModel.printType.value
    val haptics = LocalHapticFeedback.current

    var checked by rememberSaveable { mutableStateOf(false) }

    // Fixed: Request multiple Bluetooth permissions
    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            try {
                handleBluetoothPrintButton(context) {
                    calculatorViewModel.preparePrintingData(checked) {
                        calculatorCheckoutViewModel.printOrderByBluetooth(context, it, checked,calculatorOrderItems,""){
                            calculatorViewModel.clearAll()
                            onSuccess()
                        }
                    }
                }
            } catch (_: Exception) {
                Toast.makeText(context,"Some Unknown error occured",Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                context,
                "Bluetooth permissions are required for printing",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Helper function to check if all required Bluetooth permissions are granted
    fun hasAllBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_ADMIN
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Helper function to get required permissions based on Android version
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(padding)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Order Summary",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Product")
                Text("Price")
                Text("Qty")
                Text("Total")
            }

            HorizontalDivider()

            calculatorOrderItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "ITEM NAME",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        "₹${item.price.clean()}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "${item.quantity.clean()}",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "₹${item.total.clean()}",
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
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append(calculatorOrderItems.sumOf { it.quantity }.clean())
                        }
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = 15.dp)
                )
                Text(
                    buildAnnotatedString {
                        append("Total : ")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("₹${calculatorOrderItems.sumOf { it.total }.clean()}")
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
                Text("Online? :")

                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (checked) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            buildAnnotatedString {
                                append("Final Value : ")
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append(
                                        "₹${
                                            calculatorOrderItems.sumOf { it.total }
                                                .plus(calculatorOrderItems.sumOf { it.quantity })
                                                .clean()
                                        }"
                                    )
                                }
                            },
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }
            }
        }

        // 🔘 Action buttons below
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val options = listOf("BLUETOOTH", "USB")

            Text("Choose Print Type : ")
            options.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { calculatorViewModel.setPrintType(type) }
                ) {
                    RadioButton(
                        selected = selectedType == type,
                        onClick = { calculatorViewModel.setPrintType(type) }
                    )
                    Text(type, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Save Draft
            ElevatedButton(
                onClick = {
                    calculatorCheckoutViewModel.placeDraftOrder(
                        draftOrderId = draftOrderId,
                        calculatorOrderItems,
                        checked
                    )
                    calculatorViewModel.clearAll()
                    onSuccess()
                },
                modifier = Modifier.weight(1f),
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
                    calculatorCheckoutViewModel.placeFinalOrder(
                        items = calculatorOrderItems,
                        customerName = "",
                        checked
                    )
                    calculatorViewModel.clearAll()
                    onSuccess()
                },
                modifier = Modifier.weight(1f),
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
                    if (selectedType == "BLUETOOTH") {


                        if (!hasAllBluetoothPermissions()) {
                            bluetoothPermissionLauncher.launch(getRequiredBluetoothPermissions())
                            return@Button
                        }

                        try {
                            handleBluetoothPrintButton(context) {
                                calculatorViewModel.preparePrintingData(checked) {
                                    calculatorCheckoutViewModel.printOrderByBluetooth(context, it, checked,calculatorOrderItems,""){
                                        calculatorViewModel.clearAll()
                                        onSuccess()
                                    }
                                }
                            }
                        } catch (_: Exception) {
                            Toast.makeText(context,"Some Unknown error occured",Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        calculatorViewModel.preparePrintingData(checked) {
                            PrintManager(context).printTextViaUSB(it)
                        }

                    }
                },
                modifier = Modifier.weight(1f),
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
}


