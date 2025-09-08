package com.example.retailease.ui.screens.wholesale.admin.adminViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.models.Product
import com.example.retailease.models.WholesaleProduct
import com.example.retailease.repository.ProductRepository
import com.example.retailease.repository.WholesaleProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val wholesaleProductRepository: WholesaleProductRepository,
) : ViewModel() {

    private val _productsList = MutableStateFlow(emptyList<Product>())
    val productsList = _productsList.asStateFlow()

    private val _wholesaleProductsList = MutableStateFlow(emptyList<WholesaleProduct>())
    val wholesaleProductsList = _wholesaleProductsList.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()

    private val _selectedWholesaleProduct = MutableStateFlow<WholesaleProduct?>(null)
    val selectedWholesaleProduct = _selectedWholesaleProduct.asStateFlow()

    init {
        observeProducts()
    }

    private fun observeProducts() {
        viewModelScope.launch {
            launch {
                wholesaleProductRepository.getAllWholesaleProducts().collect { wholesaleProducts ->
                    _wholesaleProductsList.value = wholesaleProducts
                }
            }
            launch {
                productRepository.getAllProducts().collect { products ->
                    _productsList.value = products
                }
            }
        }
    }


//  Retail Product Repository
    fun addProduct(product: Product) {
        viewModelScope.launch{
            productRepository.insertProduct(product)
        }
    }
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }
    fun getProductById(productId: Int){
        viewModelScope.launch {
            _selectedProduct.value = productRepository.getProductById(productId)
        }
    }


//  WholeSale Product repository
    fun addWholesaleProduct(wholesaleProduct: WholesaleProduct) {
        viewModelScope.launch {
            wholesaleProductRepository.insertWholesaleProduct(wholesaleProduct)
        }
    }

    fun deleteWholesaleProduct(wholesaleProduct: WholesaleProduct) {
        viewModelScope.launch {
            wholesaleProductRepository.deleteWholesaleProduct(wholesaleProduct)

        }
    }

    fun getWholesaleProductById(wholesaleProductId: Int){
        viewModelScope.launch {
            _selectedWholesaleProduct.value  = wholesaleProductRepository.getWholesaleProductById(wholesaleProductId)
        }
    }

}