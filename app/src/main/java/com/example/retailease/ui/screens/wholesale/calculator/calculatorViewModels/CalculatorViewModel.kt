package com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.retailease.datasource.preferences.PreferencesManager
import com.example.retailease.models.DraftOrderItem
import com.example.retailease.models.Order
import com.example.retailease.models.OrderItem
import com.example.retailease.repository.DraftOrderItemRepository
import com.example.retailease.repository.OrderItemRepository
import com.example.retailease.repository.OrderRepository
import com.example.retailease.ui.components.PrintManager
import com.example.retailease.ui.components.clean
import com.example.retailease.ui.components.toBigDecimalSafe
import com.example.retailease.ui.components.toCalculatorOrderItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class CalculatorStage {
    EnteringPrice,
    EnteringQty
}

data class CalculatorOrderItem(
    val price: BigDecimal,
    val quantity: BigDecimal,
    val total: BigDecimal,
)

data class CalculatorInput(
    val price: String,
    val quantity: String,
) {
    fun priceToBigDecimal(): BigDecimal = price.toBigDecimalSafe()
    fun quantityToBigDecimal(): BigDecimal = quantity.toBigDecimalSafe()
}


@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val draftOrderItemRepository: DraftOrderItemRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {
    val openCashDrawer = preferencesManager.openCashDrawerFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )
    private val _calculatorOrderItems = MutableStateFlow<List<CalculatorOrderItem>>(emptyList())
    val calculatorOrderItems = _calculatorOrderItems.asStateFlow()

    private val _stage = MutableStateFlow(CalculatorStage.EnteringPrice)
    val stage = _stage.asStateFlow()

    private val _currentInput = MutableStateFlow(CalculatorInput("", ""))
    val currentInput = _currentInput.asStateFlow()

    private val _printType = mutableStateOf("BLUETOOTH")
    val printType: State<String> = _printType


    private val _latestCalculatorOrderItems =
        MutableStateFlow<List<CalculatorOrderItem>>(emptyList())
    val latestCalculatorOrderItems = _latestCalculatorOrderItems.asStateFlow()

    fun prepareLatestOrderPrinting(
        items: List<CalculatorOrderItem>,
        onResult: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            orderRepository.getLatestOrder().collect { latestOrder ->
                val orderItems = latestOrder.let {
                    it?.let { it1 -> orderItemRepository.getOrderItemsByOrderId(it1.orderId) }
                }
                if (orderItems != null) {
                    Log.d("Order Items", "$orderItems")
                    _latestCalculatorOrderItems.value = orderItems.toCalculatorOrderItems()
                }
            }


            val builder = StringBuilder()

            val date = Date()
            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mma", Locale.getDefault())
            val formatted = formatter.format(date)

            // Receipt title
            builder.append("[C]<font size='wide'><b><u>RECEIPT</u></b></font>\n")
            builder.append("[L]\n")

            // Date and receipt number
            builder.append("[L]Date: $formatted")
            builder.append("[R]Receipt #: ${System.currentTimeMillis().toString().takeLast(6)}\n")
            builder.append("[L]\n")

            // Separator
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")
            builder.append("[C]<b>S.NO.[C]ITEM[C]PRICE[C]QTY[C]VALUE</b>\n")
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")

            var totalCount = BigDecimal.ZERO
            var totalPrice = BigDecimal.ZERO

            var srNo = 1
            items.forEach { item ->
                val itemPrice = item.price

                val itemTotal = item.total

                // Item details - using Rs instead of ₹ for compatibility
                builder.append("[C]${srNo} ")
                builder.append("[C]ITEM NAME")
                builder.append("[C]Rs.${itemPrice.clean()}")
                builder.append("[C]${item.quantity.clean()}[C]Rs.${itemTotal.clean()}\n")

                totalPrice = totalPrice.plus(itemTotal)
                totalCount = totalCount.plus(item.quantity)
                srNo++
            }

            // Separator before totals
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")

            // Subtotal and totals
            builder.append("[L]NITEMS: ${items.size.toString()}")
            builder.append("[R]NQTY: ${totalCount.clean()}kg")
            builder.append("[R]<font size='normal'>TOTAL: Rs.${totalPrice.clean()}</font>\n")

            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")
            builder.append("[C]<b><font size='big'>GRAND TOTAL: Rs.${totalPrice.clean()}</b></font>\n")
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")

            // Footer
            builder.append("[C]<font size='normal'>Thank you, Visit Again!</font>\n")
            builder.append("[L]\n")

            val result = builder.toString()

            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }

    }

    fun printOrderByBluetooth(context: Context, printItems: String) {
        viewModelScope.launch {
            try {
                PrintManager(context).printTextViaBluetooth(printItems, openCashDrawer.value).fold(

                    onSuccess = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    },

                    onFailure = {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }

                )
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addNumber(number: String) {
        viewModelScope.launch {

            when (_stage.value) {
                CalculatorStage.EnteringPrice -> _currentInput.value =
                    _currentInput.value.copy(
                        price = appendInput(
                            _currentInput.value.price,
                            number
                        )
                    )

                CalculatorStage.EnteringQty -> _currentInput.value =
                    _currentInput.value.copy(
                        quantity = appendInput(
                            _currentInput.value.quantity,
                            number
                        )
                    )
            }
        }
    }

    private fun appendInput(current: String, newInput: String): String {
        return if (newInput.contains(".")) {
            if (current.contains(".")) current else current + newInput
        } else {
            current + newInput
        }
    }

    fun onEnterPressed() {
        viewModelScope.launch {

            when (_stage.value) {
                CalculatorStage.EnteringPrice -> {
                    if (_currentInput.value.price.contains("+")) {
                        val list = _currentInput.value.price.split("+")
                        _currentInput.value =
                            _currentInput.value.copy(price = list.sumOf { it.toBigDecimalSafe() }
                                .clean())
                    }
                    if (_currentInput.value.priceToBigDecimal()
                            .compareTo(BigDecimal.ZERO) == 0
                    ) return@launch
                    _stage.value = CalculatorStage.EnteringQty
                }

                CalculatorStage.EnteringQty -> {
                    if (_currentInput.value.quantity.contains("+")) {
                        val list = _currentInput.value.quantity.split("+")
                        _currentInput.value =
                            _currentInput.value.copy(quantity = list.sumOf { it.toBigDecimalSafe() }
                                .clean())
                    }
                    if (_currentInput.value.quantityToBigDecimal()
                            .compareTo(BigDecimal.ZERO) == 0
                    ) _currentInput.value =
                        _currentInput.value.copy(quantity = "1.0")
                    _stage.value = CalculatorStage.EnteringQty
                    val price = _currentInput.value.priceToBigDecimal()
                    val quantity = _currentInput.value.quantityToBigDecimal()
                    val total = price.multiply(quantity)

                    _calculatorOrderItems.value += CalculatorOrderItem(
                        price = price,
                        quantity = quantity,
                        total = total
                    )
                    _currentInput.value = CalculatorInput("", "")
                    _stage.value = CalculatorStage.EnteringPrice

                }
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            _calculatorOrderItems.value = emptyList()
            _currentInput.value = CalculatorInput("", "")
            _stage.value = CalculatorStage.EnteringPrice
        }

    }

    fun performDeletion() {
        viewModelScope.launch {
            when (_stage.value) {
                CalculatorStage.EnteringPrice -> _currentInput.value =
                    _currentInput.value.copy(price = _currentInput.value.price.dropLast(1))

                CalculatorStage.EnteringQty ->
                    if (_currentInput.value.quantity == "") {
                        _stage.value = CalculatorStage.EnteringPrice
                    } else {
                        _currentInput.value =
                            _currentInput.value.copy(
                                quantity = _currentInput.value.quantity.dropLast(
                                    1
                                )
                            )
                    }

            }
        }
    }

    fun loadDraftOrder(orderId: Int) {
        viewModelScope.launch {
            clearAll()
            val draftOrderItems =
                draftOrderItemRepository.getDraftOrderItemsById(draftOrderId = orderId)
            _calculatorOrderItems.value += draftOrderItems.toCalculatorOrderItems()
        }
    }

    fun deleteItem(calculatorOrderItem: CalculatorOrderItem) {
        viewModelScope.launch {
            _calculatorOrderItems.value -= calculatorOrderItem
        }
    }

    fun preparePrintingData(
        isOnline: Boolean,
        onResult: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {


            val items = calculatorOrderItems.value

            val builder = StringBuilder()

            val date = Date()
            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mma", Locale.getDefault())
            val formatted = formatter.format(date)

            // Receipt title
            builder.append("[C]<font size='wide'><b><u>RECEIPT</u></b></font>\n")
            builder.append("[L]\n")

            // Date and receipt number
            builder.append("[L]Date: $formatted")
            builder.append("[R]Receipt #: ${System.currentTimeMillis().toString().takeLast(6)}\n")
            builder.append("[L]\n")

            // Separator
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")
            builder.append("[C]<b>S.NO.[C]ITEM[C]PRICE[C]QTY[C]VALUE</b>\n")
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")

            var totalCount = BigDecimal.ZERO
            var totalPrice = BigDecimal.ZERO

            var srNo = 1
            items.forEach { item ->
                val itemPrice = if (isOnline) {
                    item.price.plus(BigDecimal.ONE)
                } else {
                    item.price
                }

                val itemTotal = if (isOnline) {
                    item.price.plus(BigDecimal.ONE).multiply(item.quantity)
                } else {
                    item.total
                }

                // Item details - using Rs instead of ₹ for compatibility
                builder.append("[C]${srNo} ")
                builder.append("[C]ITEM NAME")
                builder.append("[C]Rs.${itemPrice.clean()}")
                builder.append("[C]${item.quantity.clean()}[C]Rs.${itemTotal.clean()}\n")

                totalPrice = totalPrice.plus(itemTotal)
                totalCount = totalCount.plus(item.quantity)
                srNo++
            }

            // Separator before totals
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")

            // Subtotal and totals
            builder.append("[L]NITEMS: ${items.size.toString()}")
            builder.append("[R]NQTY: ${totalCount.clean()}kg")
            builder.append("[R]<font size='normal'>TOTAL: Rs.${totalPrice.clean()}</font>\n")

            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")
            builder.append("[C]<b><font size='big'>GRAND TOTAL: Rs.${totalPrice.clean()}</b></font>\n")
            builder.append("[C]- - - - - - - - - - - - - - - - - - - - - - - - \n")

            // Footer
            builder.append("[C]<font size='normal'>Thank you, Visit Again!</font>\n")
            builder.append("[L]\n")

            val result = builder.toString()

            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }

    }

    fun setPrintType(type: String) {
        _printType.value = type
    }


}


