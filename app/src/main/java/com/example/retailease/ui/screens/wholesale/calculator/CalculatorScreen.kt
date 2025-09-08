package com.example.retailease.ui.screens.wholesale.calculator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.retailease.ui.components.PrintManager
import com.example.retailease.ui.components.clean
import com.example.retailease.ui.components.handleBluetoothPrintButton
import com.example.retailease.ui.components.toBigDecimalSafe
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.CalculatorStage
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.CalculatorViewModel

@Composable
fun CalculatorScreen(
    padding: PaddingValues = PaddingValues(),
    calculatorViewModel: CalculatorViewModel = hiltViewModel(),
    draftOrderId: Int? = null,
    onCheckout: () -> Unit,
) {

    LaunchedEffect(draftOrderId) {
        if (draftOrderId != null) {
            calculatorViewModel.loadDraftOrder(draftOrderId)
        }
    }
    val latestCalculatorOrderItems by calculatorViewModel.latestCalculatorOrderItems.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            try {
                handleBluetoothPrintButton(context) {
                    calculatorViewModel.prepareLatestOrderPrinting(latestCalculatorOrderItems) {
                        calculatorViewModel.printOrderByBluetooth(context, it)
                    }
                }
            } catch (_: Exception) {
                Toast.makeText(context, "Some Unknown error occured", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Bluetooth permissions are required for printing", Toast.LENGTH_LONG).show()
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

    val calcItems = listOf(
        "7", "8", "9", "AC",
        "4", "5", "6", "->",
        "1", "2", "3", "+",
        ".", "0", "<-", "="
    )

    val calculatorOrderItems =
        calculatorViewModel.calculatorOrderItems.collectAsStateWithLifecycle().value
    val currentInput = calculatorViewModel.currentInput.collectAsStateWithLifecycle().value
    val stage = calculatorViewModel.stage.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(padding)
    ) {
        val isPriceStage = stage == CalculatorStage.EnteringPrice
        val isQtyStage = stage == CalculatorStage.EnteringQty

        val alpha by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(500),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Price Text
            Text(
                text = buildAnnotatedString {
                    append("Enter Price:\n")
                    withStyle(style = SpanStyle(fontWeight = if (isPriceStage) FontWeight.Bold else FontWeight.Normal)) {
                        append(currentInput.price)
                        if (isPriceStage) {
                            append(" ")
                            withStyle(SpanStyle(color = Color.Black.copy(alpha = alpha))) {
                                append("|")
                            }
                        }
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Quantity Text
            Text(
                text = buildAnnotatedString {
                    append("Enter Qty:\n")
                    withStyle(style = SpanStyle(fontWeight = if (isQtyStage) FontWeight.Bold else FontWeight.Normal)) {
                        append(currentInput.quantity)
                        if (isQtyStage) {
                            append(" ")
                            withStyle(SpanStyle(color = Color.Black.copy(alpha = alpha))) {
                                append("|")
                            }
                        }
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Total Text
            Text(
                text = "Total:\n${
                    if(currentInput.quantity.contains("+")){
                        val list = currentInput.quantity.split("+")
                        val totalQty = list.sumOf { it.toBigDecimalSafe() }
                        currentInput.priceToBigDecimal()
                            .multiply(totalQty).clean()
                    }else{
                        currentInput.priceToBigDecimal()
                            .multiply(currentInput.quantityToBigDecimal()).clean()
                    }

                }",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surfaceDim)
                .padding(horizontal = 8.dp)
        ) {
            calculatorOrderItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = { calculatorViewModel.deleteItem(item) },
                        modifier = Modifier.height(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            modifier = Modifier.fillMaxHeight(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    Row(
                        modifier = Modifier.weight(1f),

                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        Text(text = item.price.clean())
                        Text(text = "x")
                        Text(text = item.quantity.clean())
                        Text(text = "=")
                        Text(text = item.total.clean())
                    }
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Print Last Order",
                color = MaterialTheme.colorScheme.primary, // Make it look like a link
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(start = 12.dp, top = 5.dp)
                    .clickable {
                        if (!hasAllBluetoothPermissions()) {
                            bluetoothPermissionLauncher.launch(getRequiredBluetoothPermissions())
                            return@clickable
                        }

                        try {
                            handleBluetoothPrintButton(context) {
                                calculatorViewModel.prepareLatestOrderPrinting(latestCalculatorOrderItems) {
                                    calculatorViewModel.printOrderByBluetooth(context, it,)
                                }
                            }
                        } catch (_: Exception) {
                            Toast.makeText(context, "Some Unknown error occured", Toast.LENGTH_SHORT).show()
                        }
                    }
            )

            Text(
                "Total: ${calculatorOrderItems.sumOf { it.total }.clean()}",
                modifier = Modifier.padding(end = 12.dp, top = 5.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Create a 4-column grid from the list
            calcItems.chunked(4).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { label ->
                        CalculatorButtonAnimated(
                            label = label,
                            color = when (label) {
                                "AC" -> MaterialTheme.colorScheme.tertiaryContainer
                                in listOf(
                                    "+",
                                    "->",
                                    "="
                                ),
                                -> MaterialTheme.colorScheme.primaryContainer

                                else -> MaterialTheme.colorScheme.surfaceTint.copy(0.1f)
                            },
                            modifier = Modifier.weight(1f), // This makes every button equal width in the row
                            onClick = {
                                when (label) {
                                    in "0".."9", ".", "+" -> calculatorViewModel.addNumber(label)
                                    "AC" -> calculatorViewModel.clearAll()
                                    "<-" -> calculatorViewModel.performDeletion()
                                    "=" -> calculatorViewModel.onEnterPressed()
                                    "->" -> onCheckout()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CalculatorButtonAnimated(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current


    val backgroundColor by animateColorAsState(
        if (isPressed) color.copy(alpha = 0.8f) else color,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bgAnim"
    )
    val cornerRadius by animateDpAsState(
        if (isPressed) 15.dp else 50.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CornerRadius"
    )
    val overlayColor by animateColorAsState(
        if (isPressed) Color.Black.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "overlayAnim"
    )
    val paddingAnim by animateDpAsState(
        if (isPressed) 2.dp else 8.dp, label = "padAnim"
    )


    var aspectRatio: Float = 1f

    val shape = RoundedCornerShape(cornerRadius)


    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(shape)
            .background(backgroundColor)
            .background(overlayColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptics.performHapticFeedback(
                        when (label) {
                            "AC" -> HapticFeedbackType.VirtualKey
                            "=", "->" -> HapticFeedbackType.LongPress
                            else -> HapticFeedbackType.VirtualKey // fallback
                        }
                    )
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (label) {
            "<-" -> Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null)
            "->" -> Icon(Icons.Filled.ShoppingCart, contentDescription = null)
            else -> Text(
                label,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                ),
                modifier = Modifier.padding(paddingAnim)
            )
        }
    }
}

