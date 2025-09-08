package com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.datasource.preferences.PreferencesManager
import com.example.retailease.models.DraftOrder
import com.example.retailease.models.DraftOrderItem
import com.example.retailease.models.Order
import com.example.retailease.models.OrderItem
import com.example.retailease.repository.DraftOrderItemRepository
import com.example.retailease.repository.DraftOrderRepository
import com.example.retailease.repository.OrderItemRepository
import com.example.retailease.repository.OrderRepository
import com.example.retailease.ui.components.PrintManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CalculatorCheckoutViewModel @Inject constructor(
    private val draftOrderRepository: DraftOrderRepository,
    private val draftOrderItemRepository: DraftOrderItemRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    preferencesManager: PreferencesManager
) : ViewModel() {

    val openCashDrawer = preferencesManager.openCashDrawerFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )

    fun placeDraftOrder(draftOrderId: Int?, items: List<CalculatorOrderItem>, isOnline: Boolean) {
        viewModelScope.launch {
            // Adjust items if the order is "online"
            val adjustedItems = if (isOnline) {
                items.map { item ->
                    val newPrice = item.price.plus(BigDecimal.ONE)
                    item.copy(
                        price = newPrice,
                        total = newPrice.multiply(item.quantity)
                    )
                }
            } else {
                items
            }

            // Create the draft order with the potentially adjusted total price
            val draftOrder = DraftOrder(
                draftOrderId = draftOrderId ?: 0,
                totalPrice = adjustedItems.sumOf { it.total },
                totalQuantity = adjustedItems.sumOf { it.quantity }
            )

            val result = draftOrderRepository.insertDraftOrder(draftOrder).toInt()
            val orderId = if (result == -1) draftOrderId!! else result

            draftOrderItemRepository.deleteDraftOrderItemsById(orderId)

            // Save the potentially adjusted items
            adjustedItems.forEach { item ->
                draftOrderItemRepository.insertDraftOrderItem(
                    item.toDraftOrderItem(orderId)
                )
            }
        }
    }

    fun placeFinalOrder(items: List<CalculatorOrderItem>, customerName: String? = null, isOnline: Boolean) {
        viewModelScope.launch {
            // Adjust items if the order is "online"
            val adjustedItems = if (isOnline) {
                items.map { item ->
                    val newPrice = item.price.plus(BigDecimal.ONE)
                    item.copy(
                        price = newPrice,
                        total = newPrice.multiply(item.quantity)
                    )
                }
            } else {
                items
            }

            // Create the final order with the potentially adjusted total price
            val order = Order(
                totalPrice = adjustedItems.sumOf { it.total },
                totalQuantity = adjustedItems.sumOf { it.quantity },
                customerName = customerName
            )

            val orderId = orderRepository.insertOrder(order).toInt()

            // Save the potentially adjusted items
            adjustedItems.forEach { item ->
                orderItemRepository.insertOrderItem(
                    item.toOrderItem(orderId)
                )
            }
        }
    }

    fun printOrderByBluetooth(context: Context, printItems: String,isOnline: Boolean, items: List<CalculatorOrderItem>, customerName: String? = null, onSuccessfulPrint:() -> Unit){
        viewModelScope.launch {
            try {
                PrintManager(context).printTextViaBluetooth(printItems, openCashDrawer.value).fold(

                    onSuccess = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        placeFinalOrder(items = items, customerName = customerName, isOnline = isOnline)
                        onSuccessfulPrint()
                    },

                    onFailure = {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }

                )
            }catch (e: Exception){
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


}

fun CalculatorOrderItem.toDraftOrderItem(orderId: Int): DraftOrderItem {
    return DraftOrderItem(
        draftOrderId = orderId,
        productId = null,
        productName = "ITEM NAME",
        quantity = this.quantity,
        unitPrice = this.price
    )
}

fun CalculatorOrderItem.toOrderItem(orderId: Int): OrderItem {
    return OrderItem(
        orderId = orderId,
        productId = null,
        productName = "ITEM NAME",
        quantity = this.quantity,
        unitPrice = this.price
    )
}


