//package com.example.retailease.ui.screens.retail.pos.checkout
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.retailease.models.Order
//import com.example.retailease.models.OrderItem
//import com.example.retailease.repository.OrderItemRepository
//import com.example.retailease.repository.OrderRepository
//import com.example.retailease.ui.screens.retail.pos.toOrderItem
//import com.example.retailease.ui.uiModels.DraftOrderItem
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class OrderViewModel @Inject constructor(
//    private val orderRepository: OrderRepository,
//    private val orderItemRepository: OrderItemRepository,
//) : ViewModel() {
//    private val _order = MutableStateFlow<Order?>(null)
//    val order: StateFlow<Order?> = _order.asStateFlow()
//
//    private val _orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
//    val orderItems: StateFlow<List<OrderItem>> = _orderItems.asStateFlow()
//
//    fun addOrder(order: Order){
//        viewModelScope.launch {
//            orderRepository.insertOrder(order)
//        }
//    }
//
//    fun placeOrder(order: Order, items: List<DraftOrderItem>, onSuccess: (Int) -> Unit) {
//        viewModelScope.launch {
//            val orderId = orderRepository.insertOrder(order).toInt()
//            val orderItems = items.map { it.toOrderItem(orderId) }
//            orderItems.forEach { orderItemRepository.insertOrderItem(it) }
//            onSuccess(orderId)
//        }
//    }
//
//
//
//    fun loadOrder(orderId: Int){
//        viewModelScope.launch {
//            _order.value = orderRepository.getOrderById(orderId)
//            _orderItems.value = orderItemRepository.getOrderItemsByOrderId(orderId)
//        }
//    }
//
//    fun addOrderItem(orderItem: OrderItem) {
//        viewModelScope.launch {
//            orderItemRepository.insertOrderItem(orderItem)
//        }
//    }
//
//    fun getOrderItemsByOrderId(orderId: Int){
//        viewModelScope.launch {
//            _orderItems.value = orderItemRepository.getOrderItemsByOrderId(orderId)
//        }
//    }
//    fun getOrderById(orderId: Int){
//        viewModelScope.launch {
//            _order.value = orderRepository.getOrderById(orderId)
//        }
//    }
//}
