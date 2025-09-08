package com.example.retailease.ui.screens.wholesale.wsPos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.datasource.preferences.PreferencesManager
import com.example.retailease.models.SalesmanDraftOrder
import com.example.retailease.models.SalesmanDraftOrderItem
import com.example.retailease.models.SalesmanOrder
import com.example.retailease.models.SalesmanOrderItem
import com.example.retailease.models.WholesaleProduct
import com.example.retailease.repository.LedgerRepository
import com.example.retailease.repository.SalesmanDraftOrderItemRepository
import com.example.retailease.repository.SalesmanDraftOrderRepository
import com.example.retailease.repository.SalesmanOrderItemRepository
import com.example.retailease.repository.SalesmanOrderRepository
import com.example.retailease.repository.WholesaleProductRepository
import com.example.retailease.ui.components.clean
import com.example.retailease.ui.components.hasValidQuantity
import com.example.retailease.ui.components.toBigDecimalSafe
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.CalculatorOrderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class WsPosItem(
    val productId: Int?,
    val productName: String,
    val quantity: String = "",
    val unitPrice: BigDecimal,
    val quantity2: String = "",
    val unitPrice2: BigDecimal,
    val category: String?,
    val isSalesmanDiscountEnabled: Boolean = true,
)

@HiltViewModel
class WsPOSViewModel @Inject constructor(
    private val wholesaleProductRepository: WholesaleProductRepository,
    private val salesmanDraftOrderRepository: SalesmanDraftOrderRepository,
    private val salesmanDraftOrderItemRepository: SalesmanDraftOrderItemRepository,
    private val salesmanOrderRepository: SalesmanOrderRepository,
    private val salesmanOrderItemRepository: SalesmanOrderItemRepository,
    private val ledgerRepository: LedgerRepository,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val printMode = preferencesManager.printModeFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )


    private val _wholesaleProductsListState = MutableStateFlow(emptyList<WholesaleProduct>())
    val wholesaleProductsListState = _wholesaleProductsListState.asStateFlow()

    private val _categories = MutableStateFlow(emptyList<String>())
    val categories = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Namkeen")
    val selectedCategory = _selectedCategory.asStateFlow()

    val filteredProducts = combine(
        wholesaleProductsListState,
        selectedCategory
    ) { products, category ->
        products.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _wsPosItems = MutableStateFlow(emptyList<WsPosItem>())
    val wsPosItems = _wsPosItems.asStateFlow()

    private val _cartDetails = MutableStateFlow<Boolean>(false)
    val cartDetails = _cartDetails.asStateFlow()

    private val _cash = MutableStateFlow<String>("")
    val cash = _cash.asStateFlow()

    // Adding quantity and Price logic -----
    val totalCount: StateFlow<BigDecimal> = _wsPosItems.map { items ->
        items.sumOf {
            it.quantity.toBigDecimalSafe().plus(it.quantity2.toBigDecimalSafe())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BigDecimal.ZERO)

    val totalCartValue: StateFlow<BigDecimal> = _wsPosItems.map { items ->
        items.sumOf {
            it.quantity.toBigDecimalSafe().multiply(it.unitPrice).plus(
                it.quantity2.toBigDecimalSafe().multiply(it.unitPrice2)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BigDecimal.ZERO)


    //    Discount Logic ------
    private val _totalDiscountedAmount = MutableStateFlow(BigDecimal.ZERO)
    val totalDiscountedAmount = _totalDiscountedAmount.asStateFlow()

    val totalDiscountedItems: StateFlow<BigDecimal> = _wsPosItems.map { posItems ->
        val items = posItems.filter {
            it.isSalesmanDiscountEnabled
        }

        items.sumOf {
            it.quantity.toBigDecimalSafe().plus(it.quantity2.toBigDecimalSafe())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BigDecimal.ZERO)

    fun addTotalDiscountedAmount(discount: BigDecimal) {
        _totalDiscountedAmount.value = totalDiscountedItems.value.multiply(
            discount.stripTrailingZeros()
        )
    }

    val finalValue: StateFlow<BigDecimal> = combine(
        totalDiscountedAmount,
        totalCartValue
    ) { totalDiscount, totalCartValue ->
        totalCartValue.subtract(totalDiscount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BigDecimal.ZERO)


    //    Main Viewmodel -----
    init {
        observeWholeSaleProducts()
    }

    private fun observeWholeSaleProducts() {
        viewModelScope.launch {


            wholesaleProductRepository.getAllWholesaleProducts().collect { products ->
                _wholesaleProductsListState.value = products

                val uniqueCategories = products.mapNotNull {
                    it.category
                }.distinct()

                _categories.value = uniqueCategories
            }

        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    //    Cart Logic Mainly
    fun addProductToCart(wholesaleProduct: WholesaleProduct, newQty1: String, newQty2: String) {
        viewModelScope.launch {
            val existingList = _wsPosItems.value.toMutableList()
            val index = existingList.indexOfFirst { it.productId == wholesaleProduct.productId }


            val newItem = WsPosItem(
                productId = wholesaleProduct.productId,
                productName = wholesaleProduct.productName,
                quantity = newQty1,
                unitPrice = wholesaleProduct.salesmanPrice200gm,
                quantity2 = newQty2,
                unitPrice2 = wholesaleProduct.salesmanPrice500gm,
                category = wholesaleProduct.category,
                isSalesmanDiscountEnabled = wholesaleProduct.isSalesmanDiscountEnabled
            )

            if (index >= 0) {
                existingList[index] = newItem
            } else {
                existingList.add(newItem)
            }

            _wsPosItems.value = existingList
        }
    }

    fun clearCart() {
        _wsPosItems.value = emptyList()
    }

    fun dismissCartDetails() {
        _cartDetails.value = false
    }

    fun toggleCartDetails() {
        _cartDetails.value = !_cartDetails.value
    }

    fun enterCash(value: String) {
        _cash.value = value
    }

    //    Checkout logic mainly
    fun placeSalesmanDraftOrder(salesmanDraftOrderId: Int?, salesmanId: Int) {
        viewModelScope.launch {

            val salesmanDraftOrder = SalesmanDraftOrder(
                salesmanDraftOrderId = salesmanDraftOrderId ?: 0,
                salesmanId = salesmanId,
                totalPrice = totalCartValue.value,
                totalQuantity = totalCount.value,
                totalDiscount = _totalDiscountedAmount.value,
                finalPrice = finalValue.value,
                cashGiven = _cash.value.toBigDecimalSafe()
            )
            val result =
                salesmanDraftOrderRepository.insertSalesmanDraftOrder(salesmanDraftOrder).toInt()
            val finalDraftOrderId = if (result == -1) salesmanDraftOrderId!! else result

            salesmanDraftOrderItemRepository.deleteSalesmanDraftOrderItems(finalDraftOrderId)

            val items = wsPosItems.value.filter {
                it.hasValidQuantity()
            }.toSalesmanDraftOrderItems(finalDraftOrderId)

            salesmanDraftOrderItemRepository.insertSalesmanDraftOrderItems(items)
        }
    }

    fun loadSalesmanDraftOrder(salesmanDraftOrderId: Int) {
        viewModelScope.launch {
            val salesmanDraftOrderItems =
                salesmanDraftOrderItemRepository.getSalesmanDraftOrderItemsById(salesmanDraftOrderId)
            val salesmanDraftOrder =
                salesmanDraftOrderRepository.getSalesmanDraftOrderById(salesmanDraftOrderId)
            _cash.value = salesmanDraftOrder.cashGiven.clean()
            _wsPosItems.value = salesmanDraftOrderItems.toWsPosItem()
        }
    }

    fun placeSalesmanFinalOrder(salesmanId: Int) {
        viewModelScope.launch {

            val salesmanOrder = SalesmanOrder(
                salesmanId = salesmanId,
                totalPrice = totalCartValue.value,
                totalQuantity = totalCount.value,
                totalDiscount = _totalDiscountedAmount.value,
                finalPrice = finalValue.value,
                cashGiven = _cash.value.toBigDecimalSafe()
            )
            val result = salesmanOrderRepository.insertSalesmanOrder(salesmanOrder).toInt()
            val savedOrder = salesmanOrder.copy(salesmanOrderId = result)

            val items = wsPosItems.value.filter {
                it.hasValidQuantity()
            }.toSalesmanOrderItems(result)
            salesmanOrderItemRepository.insertSalesmanOrderItems(items)

            clearCart()
            enterCash("")

            ledgerRepository.processOrderForLedger(savedOrder)


        }
    }

    private suspend fun getWholesaleProductById(wholesaleProductId: Int): WholesaleProduct {
        return wholesaleProductRepository.getWholesaleProductById(wholesaleProductId)
    }

    private suspend fun List<SalesmanDraftOrderItem>.toWsPosItem(): List<WsPosItem> {
        return this.map { item ->

            val wholesaleProduct = item.productId?.let { getWholesaleProductById(it) }
            WsPosItem(
                productId = item.productId,
                productName = item.productName,
                quantity = item.quantity1.clean(),
                unitPrice = item.unitPrice1,
                quantity2 = item.quantity2.clean(),
                unitPrice2 = item.unitPrice2,
                category = wholesaleProduct?.category,
                isSalesmanDiscountEnabled = item.isSalesmanDiscountEnabled
            )
        }
    }

    fun addMoreItemsAtCheckout(items: List<CalculatorOrderItem>) {
        viewModelScope.launch {
            _wsPosItems.value = _wsPosItems.value + items.toWsPosItem()
        }
    }

    fun handlePrinting(salesmanId: Int, onDataReady: (String) -> Unit) {
        viewModelScope.launch {
            when (printMode.value) {
                true -> preparePrintingDataByCategory(salesmanId, onDataReady)
                false -> preparePrintingData(salesmanId, onDataReady)
            }
        }
    }

    private fun preparePrintingData(
        salesmanId: Int,
        onDataReady: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val items = wsPosItems.value.filter { it.hasValidQuantity() }
            val builder = StringBuilder()

            val date = Date()
            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mma", Locale.getDefault())
            val formatted = formatter.format(date)

            // Title
            builder.append("[C]<font size='wide'><b><u>SL RECEIPT</u></b></font>\n")
            builder.append("[L]\n")

            // Date & Receipt No.
            builder.append("[L]Date: $formatted")
            builder.append("[R]Receipt #: ${System.currentTimeMillis().toString().takeLast(6)}\n")
            builder.append("[L]\n")

            // Separator
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")
            builder.append("[L]<b>${formatHeader()}</b>\n")
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")

            items.forEach { item ->
                if (item.quantity.toBigDecimalSafe() > BigDecimal.ZERO) {
                    builder.append(
                        "[L]" + formatLine(
                            name = if (!item.productName.endsWith("/-", ignoreCase = true)  && item.category != "Extra") {
                                "${item.productName.uppercase()} 200g"
                            } else {
                                item.productName.uppercase()
                            },
                            price = "Rs.${item.unitPrice.clean()}",
                            qty = item.quantity,
                            total = "Rs.${item.unitPrice.multiply(item.quantity.toBigDecimalSafe()).clean()}"
                        ) + "\n"
                    )
                }

                if (item.quantity2.toBigDecimalSafe() > BigDecimal.ZERO) {
                    builder.append(
                        "[L]" + formatLine(
                            name = if (!item.productName.endsWith("/-", ignoreCase = true)  && item.category != "Extra") {
                                "${item.productName.uppercase()} 500g"
                            } else {
                                item.productName.uppercase()
                            },
                            "Rs.${item.unitPrice2.clean()}",
                            item.quantity2,
                            "Rs.${item.unitPrice2.multiply(item.quantity2.toBigDecimalSafe()).clean()}"
                        ) + "\n"
                    )
                }
            }


            // Final separator
            builder.append("[C]================================================\n")

            // Totals from computed StateFlows
            builder.append("[L]TOTAL QTY: ${totalCount.value.clean()}kg")
            builder.append("[R]TOTAL: <b>Rs.${totalCartValue.value.clean()}</b>\n")
            if (_totalDiscountedAmount.value > BigDecimal.ZERO) {
                builder.append("[R]Less: <b>- ${_totalDiscountedAmount.value.clean()}</b>\n")
            }


            val currentBalance = ledgerRepository.getCurrentBalance(salesmanId)

            val grandTotal = when {
                currentBalance.compareTo(BigDecimal.ZERO) == 0 -> finalValue.value
                else -> finalValue.value.add(currentBalance)
            }


            val pendingAmount = if (cash.value.isNotBlank() && cash.value.toBigDecimalSafe() > BigDecimal.ZERO) {
                grandTotal.subtract(cash.value.toBigDecimalSafe())
            } else {
                grandTotal
            }


            when {
                currentBalance.compareTo(BigDecimal.ZERO) > 0 -> {
                    builder.append("[R]Outstanding: <b>${currentBalance.clean()}</b>\n")
                }

                currentBalance.compareTo(BigDecimal.ZERO) < 0 -> {
                    builder.append("[R]Jama: <b>${currentBalance.clean()}</b>\n")
                }

            }


            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")
            builder.append("[C]<b><font size='big'>GRAND TOTAL: Rs.${grandTotal.clean()}</b></font>\n")
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")

            if (cash.value.isNotBlank() && cash.value.toBigDecimalSafe() > BigDecimal.ZERO) {
                builder.append("[C]Cash: [C]<b>- ${cash.value}</b>\n")
                builder.append("[C]<font size='normal'>Pending Amount: [C]<b>Rs.${pendingAmount.clean()}</b></font>\n")
            }



            builder.append("[C]================================================\n")
            builder.append("[L]\n")
            // Footer
            builder.append("[C]<font size='normal'>Thank you for your business! Visit again!</font>\n")
            builder.append("[L]\n")
            val result = builder.toString()

            withContext(Dispatchers.Main) {
                onDataReady(result)
            }
        }


    }

    private fun preparePrintingDataByCategory(
        salesmanId: Int,
        onDataReady: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val items = wsPosItems.value.filter { it.hasValidQuantity() }
            val builder = StringBuilder()

            val date = Date()
            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mma", Locale.getDefault())
            val formatted = formatter.format(date)

            // Title
            builder.append("[C]<font size='wide'><b><u>SL RECEIPT</u></b></font>\n")
            builder.append("[L]\n")

            // Date & Receipt No.
            builder.append("[L]Date: $formatted")
            builder.append("[R]Receipt #: ${System.currentTimeMillis().toString().takeLast(6)}\n")
            builder.append("[L]\n")

            // Separator
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")
            builder.append("[L]<b>${formatHeader()}</b>\n")
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")


            val namkeenItems = items.filter { cartItem ->
                cartItem.category == "Namkeen"
            }
            val totalNamkeenItems200gm = namkeenItems.sumOf { it.quantity.toBigDecimalSafe() }
            val totalNamkeenItems200gmPrice = namkeenItems.first().unitPrice
            val finalNamkeenItems200gmPrice =
                totalNamkeenItems200gm.multiply(totalNamkeenItems200gmPrice)
            val totalNamkeenItems500gm = namkeenItems.sumOf { it.quantity2.toBigDecimalSafe() }
            val totalNamkeenItems500gmPrice = namkeenItems.first().unitPrice2
            val finalNamkeenItems500gmPrice =
                totalNamkeenItems500gm.multiply(totalNamkeenItems500gmPrice)

            if (totalNamkeenItems200gm > BigDecimal.ZERO) {
                builder.append(
                    "[L]" + formatLine(
                        "NAMKEEN 200g",
                        "Rs.${totalNamkeenItems200gmPrice.clean()}",
                        totalNamkeenItems200gm.clean(),
                        "Rs.${finalNamkeenItems200gmPrice.clean()}"
                    ) + "\n"
                )
            }
            if (totalNamkeenItems500gm > BigDecimal.ZERO) {
                builder.append(
                    "[L]" + formatLine(
                        "NAMKEEN 500g",
                        "Rs.${totalNamkeenItems500gmPrice.clean()}",
                        totalNamkeenItems500gm.clean(),
                        "Rs.${finalNamkeenItems500gmPrice.clean()}"
                    ) + "\n"
                )
            }



            val remainingItems = items.filter { cartItem ->
                cartItem.category != "Namkeen"
            }

            remainingItems.forEach { item ->


                if (item.quantity.toBigDecimalSafe() > BigDecimal.ZERO) {
                    builder.append(
                        "[L]" + formatLine(
                            name = if (!item.productName.endsWith("/-", ignoreCase = true)  && item.category != "Extra") {
                                "${item.productName.uppercase()} 200g"
                            } else {
                                item.productName.uppercase()
                            }
                            ,
                            price = "Rs.${item.unitPrice.clean()}",
                            qty = item.quantity,
                            total = "Rs.${item.unitPrice.multiply(item.quantity.toBigDecimalSafe()).clean()}"
                        ) + "\n"
                    )
                }

                if (item.quantity2.toBigDecimalSafe() > BigDecimal.ZERO) {
                    builder.append(
                        "[L]" + formatLine(
                            name = if (!item.productName.endsWith("/-", ignoreCase = true)  && item.category != "Extra") {
                                "${item.productName.uppercase()} 500g"
                            } else {
                                item.productName.uppercase()
                            },
                            "Rs.${item.unitPrice2.clean()}",
                            item.quantity2,
                            "Rs.${item.unitPrice2.multiply(item.quantity2.toBigDecimalSafe()).clean()}"
                        ) + "\n"
                    )
                }

            }

            // Final separator
            builder.append("[C]================================================\n")

            // Totals from computed StateFlows
            builder.append("[L]TOTAL QTY: ${totalCount.value.clean()}kg")
            builder.append("[R]TOTAL: <b>Rs.${totalCartValue.value.clean()}</b>\n")
            if (_totalDiscountedAmount.value > BigDecimal.ZERO) {
                builder.append("[R]Less: <b>- ${_totalDiscountedAmount.value.clean()}</b>\n")
            }


            val currentBalance = ledgerRepository.getCurrentBalance(salesmanId)

            val grandTotal = when {
                currentBalance.compareTo(BigDecimal.ZERO) == 0 -> finalValue.value
                else -> finalValue.value.add(currentBalance)
            }


            val pendingAmount = if (cash.value.isNotBlank() && cash.value.toBigDecimalSafe() > BigDecimal.ZERO) {
                grandTotal.subtract(cash.value.toBigDecimalSafe())
            } else {
                grandTotal
            }


            when {
                currentBalance.compareTo(BigDecimal.ZERO) > 0 -> {
                    builder.append("[R]Outstanding: <b>${currentBalance.clean()}</b>\n")
                }

                currentBalance.compareTo(BigDecimal.ZERO) < 0 -> {
                    builder.append("[R]Jama: <b>${currentBalance.clean()}</b>\n")
                }

            }


            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")
            builder.append("[C]<b><font size='big'>GRAND TOTAL: Rs.${grandTotal.clean()}</b></font>\n")
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")

            if (cash.value.isNotBlank() && cash.value.toBigDecimalSafe() > BigDecimal.ZERO) {
                builder.append("[C]Cash: [C]<b>- ${cash.value}</b>\n")
                builder.append("[C]<font size='normal'>Pending Amount: [C]<b>Rs.${pendingAmount.clean()}</b></font>\n")
            }



            builder.append("[C]================================================\n")
            builder.append("[L]\n")
            // Footer
            builder.append("[C]<font size='normal'>Thank you for your business! Visit again!</font>\n")
            builder.append("[L]\n")
            val result = builder.toString()

            withContext(Dispatchers.Main) {
                onDataReady(result)  // ← Now safe to show Toast
            }
        }

    }


}


private fun List<WsPosItem>.toSalesmanDraftOrderItems(salesmanDraftOrderId: Int): List<SalesmanDraftOrderItem> {
    return this.map {
        SalesmanDraftOrderItem(
            salesmanDraftOrderId = salesmanDraftOrderId,
            productId = it.productId,
            productName = it.productName,
            quantity1 = it.quantity.toBigDecimalSafe(),
            unitPrice1 = it.unitPrice,
            quantity2 = it.quantity2.toBigDecimalSafe(),
            unitPrice2 = it.unitPrice2,
            isSalesmanDiscountEnabled = it.isSalesmanDiscountEnabled
        )
    }
}


private fun List<CalculatorOrderItem>.toWsPosItem(): List<WsPosItem> {
    return this.map {
        WsPosItem(
            productId = null,
            productName = "Item ${it.price.clean()}/-",
            quantity = it.quantity.clean(),
            unitPrice = it.price,
            quantity2 = "",
            unitPrice2 = BigDecimal.ZERO,
            category = null,
            isSalesmanDiscountEnabled = false
        )
    }
}

private fun List<WsPosItem>.toSalesmanOrderItems(salesmanOrderId: Int): List<SalesmanOrderItem> {
    return this.map {
        SalesmanOrderItem(
            salesmanOrderId = salesmanOrderId,
            productId = it.productId,
            quantity = it.quantity.toBigDecimalSafe(),
            quantity2 = it.quantity2.toBigDecimalSafe()
        )
    }
}

private fun formatHeader(
    nameHeader: String = "NAME",
    priceHeader: String = "PRICE",
    qtyHeader: String = "QTY",
    totalHeader: String = "VALUE",
    nameWidth: Int = 22,
    priceWidth: Int = 8,
    qtyWidth: Int = 5,
    totalWidth: Int = 13
): String {
    return nameHeader.padEnd(nameWidth) +
            priceHeader.padStart(priceWidth) +
            qtyHeader.padStart(qtyWidth) +
            totalHeader.padStart(totalWidth)
}

private fun formatLine(
    name: String,
    price: String,
    qty: String,
    total: String,
    nameWidth: Int = 22,
    priceWidth: Int = 8,
    qtyWidth: Int = 5,
    totalWidth: Int = 13
): String {
    // Ellipsize if name too long
    val displayName = if (name.length > nameWidth) {
        name.take(nameWidth - 1) + "…"
    } else {
        name
    }

    return displayName.padEnd(nameWidth) +
            price.padStart(priceWidth) +
            qty.padStart(qtyWidth) +
            total.padStart(totalWidth)
}