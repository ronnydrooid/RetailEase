package com.example.retailease.ui.screens.wholesale.khatabook

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.retailease.models.SalesmanLedger
import com.example.retailease.models.TransactionType
import com.example.retailease.ui.components.clean
import com.example.retailease.ui.components.toBigDecimalSafe
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun KhatabookHistoryScreen(
    padding: PaddingValues = PaddingValues(),
    salesmanId: Int? = null,
    khatabookViewModel: KhatabookViewModel = hiltViewModel(),
) {
    val ledgerEntries by khatabookViewModel.ledgerEntries.collectAsStateWithLifecycle()
    val currentBalance by khatabookViewModel.currentBalance.collectAsStateWithLifecycle()
    val isLoading by khatabookViewModel.isLoading.collectAsStateWithLifecycle()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showSaleDialog by remember { mutableStateOf(false) }

    // Track scroll state for FAB animation
    val listState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    @Composable
    fun AnimatedExtendedFAB(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        expanded: Boolean,
        icon: ImageVector,
        text: String,
        containerColor: Color,
        contentDescription: String,
    ) {
        val animatedWidth by animateDpAsState(
            targetValue = if (expanded) 200.dp else 56.dp,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            label = "fab_width"
        )

        FloatingActionButton(
            onClick = onClick,
            modifier = modifier
                .height(56.dp)
                .padding(horizontal = 8.dp)
                .width(animatedWidth),
            containerColor = containerColor,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = if (expanded) 16.dp else 0.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(20.dp)
                )

                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(200)) +
                            slideOutHorizontally(animationSpec = tween(200))
                ) {
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
        @Composable
        fun TransactionDialog(
            title: String,
            subtitle: String,
            onDismiss: () -> Unit,
            onConfirm: (amount: String, description: String) -> Unit,
            buttonColor: Color,
            buttonText: String,
        ) {
            var amount by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Amount TextField
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { newValue ->
                                // Only allow numbers and decimal point
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    amount = newValue
                                }
                            },
                            label = { Text("Amount (₹)") },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Text(
                                    text = "₹",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                            }
                        )

                        // Description TextField
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            placeholder = { Text("Enter description...") },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (amount.isNotBlank()) {
                                onConfirm(amount, description.ifBlank { "Transaction" })
                            }
                        },
                        enabled = amount.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(buttonText)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Payment Dialog (Credit Entry)
        if (showPaymentDialog) {
            TransactionDialog(
                title = "पेमेंट मिला",
                subtitle = "Credit Entry",
                onDismiss = { showPaymentDialog = false },
                onConfirm = { amount, description ->
                    // Handle credit entry
                    val ledgerEntry = salesmanId?.let {
                        SalesmanLedger(
                            salesmanId = it,
                            relatedOrderId = null,
                            amount = amount.toBigDecimalSafe(),
                            transactionType = TransactionType.CREDIT,
                            description = description,
                            runningBalance = currentBalance.subtract(amount.toBigDecimalSafe())
                        )
                    }
                    if (ledgerEntry != null) {
                        khatabookViewModel.insertLedgerEntry(ledgerEntry)
                    }
                    showPaymentDialog = false
                },
                buttonColor = Color(0xFF4CAF50),
                buttonText = "Add Payment"
            )
        }

        // Sale Dialog (Debit Entry)
        if (showSaleDialog) {
            TransactionDialog(
                title = "सेल जोड़ें",
                subtitle = "Debit Entry",
                onDismiss = { showSaleDialog = false },
                onConfirm = { amount, description ->
                    // Handle debit entry

                    val ledgerEntry = salesmanId?.let {
                        SalesmanLedger(
                            salesmanId = it,
                            relatedOrderId = null,
                            amount = amount.toBigDecimalSafe(),
                            transactionType = TransactionType.DEBIT,
                            description = description,
                            runningBalance = currentBalance.add(amount.toBigDecimalSafe())
                        )
                    }
                    if (ledgerEntry != null) {
                        khatabookViewModel.insertLedgerEntry(ledgerEntry)
                    }
                    showSaleDialog = false
                },
                buttonColor = Color(0xFFF44336),
                buttonText = "Add Sale"
            )
        }

    }


    LaunchedEffect(salesmanId) {
        if (salesmanId != null) {
            khatabookViewModel.loadLedgerForSalesman(salesmanId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Section - Sticky at top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Khatabook",
                    style = MaterialTheme.typography.headlineMedium
                )

                IconButton(
                    onClick = {
                        if (salesmanId != null) {
                            khatabookViewModel.refreshLedger(salesmanId)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Balance Card - Sticky at top
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        currentBalance.compareTo(BigDecimal.ZERO) > 0 -> Color(0xFFF44336).copy(
                            alpha = 0.1f
                        )  // Light red for outstanding
                        currentBalance.compareTo(BigDecimal.ZERO) == 0 -> Color.Gray.copy(alpha = 0.1f)        // Light gray for clear
                        else -> Color(0xFF4CAF50).copy(alpha = 0.1f)                                           // Light green for advance
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Current Balance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "₹${currentBalance.clean()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            currentBalance.compareTo(BigDecimal.ZERO) > 0 -> Color(0xFFF44336)  // RED for outstanding (बकाया)
                            currentBalance.compareTo(BigDecimal.ZERO) == 0 -> Color.Gray        // GRAY for clear (क्लियर)
                            else -> Color(0xFF4CAF50)                                           // GREEN for advance (जमा)
                        }
                    )
                    Text(
                        text = when {
                            currentBalance.compareTo(BigDecimal.ZERO) > 0 -> "बकाया"      // RED (they owe you - urgent)
                            currentBalance.compareTo(BigDecimal.ZERO) == 0 -> "क्लियर"   // GRAY (neutral - all clear)
                            else -> "जमा"                                                // GREEN (they paid extra - good)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = when {
                            currentBalance.compareTo(BigDecimal.ZERO) > 0 -> Color(0xFFF44336)  // Red
                            currentBalance.compareTo(BigDecimal.ZERO) == 0 -> Color.Gray        // Gray
                            else -> Color(0xFF4CAF50)                                           // Green
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Table Header - Sticky at top
            if (!isLoading && ledgerEntries.isNotEmpty()) {
                TableHeader()
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Scrollable Transactions List - This scrolls behind FABs
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (ledgerEntries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState, // Connect to scroll state
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 68.dp), // Space for FABs
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(ledgerEntries) { ledgerEntry ->
                        LedgerEntryItemTable(ledgerEntry = ledgerEntry)
                    }
                }
            }
        }

        // Beautiful Extended FABs at the bottom - with scroll animation!
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Credit/Payment Received Button (Green) - Animated
            AnimatedExtendedFAB(
                onClick = {
                    showPaymentDialog = true
                },
                modifier =  if(!isScrolled) {Modifier.weight(1f)} else{Modifier},
                expanded = !isScrolled,
                icon = Icons.Default.Add,
                text = "पेमेंट मिला",
                containerColor = Color(0xFF4CAF50),
                contentDescription = "Payment Received"
            )

            // Debit/Sale Button (Red) - Animated
            AnimatedExtendedFAB(
                onClick = {
                    showSaleDialog = true
                },
                modifier = if(!isScrolled) {Modifier.weight(1f)} else{Modifier},
                expanded = !isScrolled,
                icon = Icons.Default.ShoppingCart,
                text = "सेल जोड़ें",
                containerColor = Color(0xFFF44336),
                contentDescription = "Add Sale"
            )
        }
    }
}

@Composable
fun TableHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 10.dp)
        ) {
            Text(
                text = "बकाया",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                text = "जमा",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Balance",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun LedgerEntryItemTable(ledgerEntry: SalesmanLedger) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column {
            // Header with description and date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ledgerEntry.description ?: "Transaction",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (ledgerEntry.relatedOrderId != null) {
                        Text(
                            text = "Order: ${ledgerEntry.relatedOrderId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                Text(
                    text = dateFormat.format(ledgerEntry.transactionDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Divider
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))

            // Banking table row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 10.dp)
            ) {
                // DR Column
                Text(
                    text = if (ledgerEntry.transactionType == TransactionType.DEBIT)
                        "₹${ledgerEntry.amount.clean()}"
                    else
                        "-",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (ledgerEntry.transactionType == TransactionType.DEBIT)
                        Color(0xFFF44336) else Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )

                // CR Column
                Text(
                    text = if (ledgerEntry.transactionType == TransactionType.CREDIT)
                        "₹${ledgerEntry.amount.clean()}"
                    else
                        "-",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (ledgerEntry.transactionType == TransactionType.CREDIT)
                        Color(0xFF4CAF50) else Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Balance Column
                Text(
                    text = "₹${(ledgerEntry.runningBalance ?: BigDecimal.ZERO).clean()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        (ledgerEntry.runningBalance
                            ?: BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0 ->
                            Color(0xFFF44336)  // Red for outstanding
                        (ledgerEntry.runningBalance
                            ?: BigDecimal.ZERO).compareTo(BigDecimal.ZERO) == 0 ->
                            Color.Gray         // Gray for zero
                        else ->
                            Color(0xFF4CAF50)  // Green for advance
                    },
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}