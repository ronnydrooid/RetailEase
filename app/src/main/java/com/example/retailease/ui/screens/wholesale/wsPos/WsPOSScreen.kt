package com.example.retailease.ui.screens.wholesale.wsPos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.retailease.models.WholesaleProduct
import com.example.retailease.ui.components.clean
import com.example.retailease.ui.components.hasValidQuantity
import com.example.retailease.ui.components.toBigDecimalSafe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WsPOSScreen(
    padding: PaddingValues,
    wsPosViewModel: WsPOSViewModel = hiltViewModel(),
    onNavigateCheckout: () -> Unit,
) {
    val productsList = wsPosViewModel.filteredProducts.collectAsStateWithLifecycle().value
    val wsPosItems = wsPosViewModel.wsPosItems.collectAsStateWithLifecycle().value
    val cartDetails = wsPosViewModel.cartDetails.collectAsStateWithLifecycle().value
    val categories = wsPosViewModel.categories.collectAsStateWithLifecycle().value
    val selectedCategory = wsPosViewModel.selectedCategory.collectAsStateWithLifecycle().value

    // Get values for bottom bar
    val totalCount = wsPosViewModel.totalCount.collectAsStateWithLifecycle().value
    val totalPrice = wsPosViewModel.totalCartValue.collectAsStateWithLifecycle().value


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
    ) {
        Column(
            modifier = Modifier
                .weight(1f) // This makes it take up most of the available space
                .fillMaxWidth()
        ) {

            CategoryFilterRow(
                categories = categories,
                selectedCategory = selectedCategory,
                onSelectedCategory = {
                    wsPosViewModel.selectCategory(category = it)
                }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = 8.dp // Small padding at the bottom
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(70.dp))
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "200gm",
                            modifier = Modifier.width(92.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "500gm",
                            modifier = Modifier.width(92.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                    }
                }
                itemsIndexed(productsList, key = { _, item -> item.productId }) { index, product ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProductRow(
                            product = product,
                            item = wsPosItems.find { it.productId == product.productId },
                            onQuantity1Change = { wholesaleProduct, newQty ->
                                val itemIndex =
                                    wsPosItems.indexOfFirst { it.productId == wholesaleProduct.productId }
                                var qty2 = ""
                                if (itemIndex >= 0) {
                                    qty2 = wsPosItems[itemIndex].quantity2
                                }
                                wsPosViewModel.addProductToCart(
                                    wholesaleProduct,
                                    newQty,
                                    qty2
                                )
                            },
                            onQuantity2Change = { wholesaleProduct, newQty2 ->
                                val itemIndex =
                                    wsPosItems.indexOfFirst { it.productId == wholesaleProduct.productId }
                                var qty1 = ""
                                if (itemIndex >= 0) {
                                    qty1 = wsPosItems[itemIndex].quantity
                                }
                                wsPosViewModel.addProductToCart(
                                    wholesaleProduct,
                                    qty1,
                                    newQty2
                                )
                            }
                        )
                        HorizontalDivider(Modifier.fillMaxWidth())
                    }
                }
            }
        }

        // 2nd Column - Bottom cart bar (aligned to bottom)
        if (totalCount > BigDecimal.ZERO) {
            POSBottomBar(
                totalCount = totalCount,
                totalPrice = totalPrice,
                onClick = {
                    wsPosViewModel.toggleCartDetails()
                },
                modifier = Modifier.navigationBarsPadding() // Add navigation bar padding here
            )
        }
    }

    // Modal Bottom Sheet for cart details (outside the main Column structure)
    if (cartDetails) {

        ModalBottomSheet(
            onDismissRequest = {
                wsPosViewModel.dismissCartDetails()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 🧾 Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Product",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "200g",
                        modifier = Modifier.width(64.dp),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "500g",
                        modifier = Modifier.width(64.dp),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                HorizontalDivider(thickness = 1.dp)

                // 🧺 Cart Items
                wsPosItems.filter { it.hasValidQuantity() }.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.productName,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = item.quantity.toBigDecimalSafe().clean(),
                            modifier = Modifier.width(64.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = item.quantity2.toBigDecimalSafe().clean(),
                            modifier = Modifier.width(64.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Optional: Slight divider between items
                    if (index != wsPosItems.lastIndex) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🧮 Summary
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryRow(
                        label = "Total Items:",
                        value = wsPosViewModel.totalCount.collectAsStateWithLifecycle().value.clean()
                    )
                    SummaryRow(
                        label = "Total Price:",
                        value = "₹${wsPosViewModel.totalCartValue.collectAsStateWithLifecycle().value.clean()}"
                    )
                }
                ElevatedButton(
                    onClick = onNavigateCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = "Checkout",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Checkout", style = MaterialTheme.typography.labelMedium.copy(
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
}

@Composable
fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onSelectedCategory: (String) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 8.dp
        )
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onSelectedCategory(category) },
                label = { Text(category) },
                leadingIcon = {
                    if (category == selectedCategory) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "selected",
                            modifier = Modifier.size(18.dp)
                        )
                    } else {

                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                ),
                border = if (selectedCategory == category) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                }
            )
        }
    }
}

@Composable
fun POSBottomBar(
    totalCount: BigDecimal,
    totalPrice: BigDecimal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${totalCount.clean()} items", fontWeight = FontWeight.Bold)
                Text("₹ ${totalPrice.clean()}")
            }
            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
        }
    }
}


@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductRow(
    product: WholesaleProduct,
    item: WsPosItem? = null,
    onQuantity1Change: (WholesaleProduct, String) -> Unit,
    onQuantity2Change: (WholesaleProduct, String) -> Unit,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val quantity = item?.quantity?.takeIf { quantity ->
        quantity.isNotBlank()
    } ?: ""
    val quantity2 = item?.quantity2?.takeIf { quantity2 ->
        quantity2.isNotBlank()
    } ?: ""

    val imageFile =
        if (product.imagePath.isNullOrEmpty() || !product.imagePath.startsWith("/data")) {
            "https://cdn-icons-png.freepik.com/512/2921/2921826.png"

        } else {
            File(product.imagePath)
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedCard(
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(8.dp)
        ) {

            AsyncImage(
                model = imageFile,
                contentDescription = product.productName,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
//            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = product.productName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 2
            )
            Text(
                text = "₹ ${product.salesmanPrice200gm.toInt()}/-",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "₹ ${product.salesmanPrice500gm.toInt()}/-",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { input ->
                    val filtered = input.filterIndexed { index, it ->
                        it.isDigit() || (it == '.' && !input.substring(0..<index).contains('.'))
                    }
                    onQuantity1Change(product, filtered)
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                placeholder = { Text("kg") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                delay(300)
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
                    .width(76.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    modifier = Modifier.size(44.dp),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        val currentVal = quantity.toBigDecimalSafe()
                        val quantityChange = currentVal.plus(BigDecimal.ONE).toString()
                        onQuantity1Change(product, quantityChange)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    modifier = Modifier.size(44.dp),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        val currentVal = quantity.toBigDecimalSafe()
                        var quantityChange = ""
                        if (currentVal > BigDecimal.ZERO) {
                            quantityChange = currentVal.subtract(BigDecimal.ONE).toString()
                        }
                        onQuantity1Change(product, quantityChange)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircleOutline,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = quantity2,
                onValueChange = { input ->
                    val filtered = input.filterIndexed { index, it ->
                        it.isDigit() || (it == '.' && !input.substring(0..<index).contains('.'))
                    }
                    onQuantity2Change(product, filtered)
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                placeholder = { Text("kg") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                delay(300)
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
                    .width(76.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    modifier = Modifier.size(44.dp),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        val currentVal = quantity2.toBigDecimalSafe()
                        val quantity2Change = (currentVal.plus(BigDecimal.ONE)).toString()
                        onQuantity2Change(product, quantity2Change)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    modifier = Modifier.size(44.dp),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        val currentVal = quantity2.toBigDecimalSafe()
                        var quantity2Change = ""
                        if (currentVal.compareTo(BigDecimal.ZERO) > 0) {
                            quantity2Change = currentVal.subtract(BigDecimal.ONE).toString()
                        }
                        onQuantity2Change(product, quantity2Change)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircleOutline,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}