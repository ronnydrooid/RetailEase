package com.example.retailease.ui.components

import com.example.retailease.models.DraftOrderItem
import com.example.retailease.models.OrderItem
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.CalculatorOrderItem
import com.example.retailease.ui.screens.wholesale.wsPos.WsPosItem
import java.math.BigDecimal

fun String.toBigDecimalSafe(): BigDecimal {
    return this.toBigDecimalOrNull()?.stripTrailingZeros() ?: BigDecimal.ZERO
}
fun WsPosItem.hasValidQuantity(): Boolean {
    return (quantity.isNotBlank() && quantity.toBigDecimalSafe() != BigDecimal.ZERO) ||
            (quantity2.isNotBlank() && quantity2.toBigDecimalSafe() != BigDecimal.ZERO)
}
fun BigDecimal.clean(): String{
    return this.stripTrailingZeros().toPlainString()
}

@JvmName("draftItemsToCalculatorItems")
fun List<DraftOrderItem>.toCalculatorOrderItems(): List<CalculatorOrderItem> {
    return this.map {
        CalculatorOrderItem(
            price = it.unitPrice,
            quantity = it.quantity,
            total = it.unitPrice.multiply(it.quantity)
        )
    }
}

@JvmName("orderItemsToCalculatorItems")
fun List<OrderItem>.toCalculatorOrderItems(): List<CalculatorOrderItem> {
    return this.map {
        CalculatorOrderItem(
            price = it.unitPrice,
            quantity = it.quantity,
            total = it.unitPrice.multiply(it.quantity)
        )
    }
}
