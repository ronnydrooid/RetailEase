//package com.example.retailease.ui.screens.retail.pos
//
//import androidx.compose.runtime.State
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.retailease.models.Product
//import com.example.retailease.repository.ProductRepository
//import com.example.retailease.ui.uiModels.DraftOrderItem
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import okhttp3.internal.toImmutableList
//import javax.inject.Inject
//
//@HiltViewModel
//class POSViewModel @Inject constructor(private val productRepository: ProductRepository) :
//    ViewModel() {
//    //    MainPOSScreen
//    private val _cartItems = mutableStateListOf<DraftOrderItem>()
//    val cartItems: List<DraftOrderItem>
//        get() {
//            return _cartItems.toList()
//        }
//
//    private val _showCartDetails = mutableStateOf(false)
//    val showCartDetails: State<Boolean> = _showCartDetails
//
//    init{
//        observeProducts()
//    }
//    fun addProductToCart(product: Product) {
//        val index = _cartItems.indexOfFirst { it.productId == product.productId }
//        if (index >= 0) {
//            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity + 1)
//        } else {
//            _cartItems.add(
//                DraftOrderItem(
//                    productId = product.productId,
//                    quantity = 1,
//                    unitPrice = product.price,
//                    productName = product.productName
//                )
//            )
//        }
//    }
//
//    fun removeProductFromCart(product: Product) {
//        val index = _cartItems.indexOfFirst { it.productId == product.productId }
//        if (index >= 0) {
//            val item = _cartItems[index]
//            if (item.quantity > 1) {
//                _cartItems[index] = item.copy(quantity = item.quantity - 1)
//            } else {
//                _cartItems.removeAt(index)
//            }
//        }
//    }
//
//
//    fun toggleCartDetails() {
//        _showCartDetails.value = !_showCartDetails.value
//    }
//
//    fun dismissCartDetails() {
//        _showCartDetails.value = false
//    }
//
//    //    AddProductsScreen
//
//    private val productsList: List<Product> = emptyList()
//    private val _productsListState = MutableStateFlow(productsList)
//    val productsListState = _productsListState.asStateFlow()
//
//    private fun observeProducts() {
//        viewModelScope.launch {
//            productRepository.getAllProducts()
//                .collect{productsList ->
//                    _productsListState.value = productsList
//                }
//        }
//    }
//
//    fun addProduct(product: Product){
//        viewModelScope.launch {
//            productRepository.insertProduct(product)
//        }
//    }
//
//    fun deleteProduct(product: Product){
//        viewModelScope.launch {
//            productRepository.deleteProduct(product)
//        }
//    }
//
//}